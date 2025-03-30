/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.services.oaipmh;

import at.roesel.oadataprocessor.model.oaipmh.OaiPmhApiException;
import at.roesel.oadataprocessor.model.oaipmh.jabx.*;
import at.roesel.oadataprocessor.model.oaipmh.jabx.dc.OaiDcType;
import at.roesel.oadataprocessor.services.common.CollectAllResultsResponseHandler;
import at.roesel.oadataprocessor.services.common.RestClient;
import at.roesel.oadataprocessor.services.common.ResultResponseHandler;
import at.roesel.oadataprocessor.support.DateUtil;
import at.roesel.oadataprocessor.support.FileSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static at.roesel.oadataprocessor.support.FileSupport.nameFromUrl;

/*
 * Implementation for an OaiPmh Service Consumer
 */
public class OaiPmhImporterImpl extends RestClient implements OaiPmhImporter {

    private final Logger logger = LoggerFactory.getLogger(OaiPmhImporterImpl.class);

    private final static String verbListIdentify = "Identify";
    private final static String verbListIdentifiers = "ListIdentifiers";
    private final static String verbListRecords = "ListRecords";
    private final static String verbListSets = "ListSets";
    private final static String verbListMetadataFormats = "ListMetadataFormats";
    private final static String verbGetRecord = "GetRecord";

    private final static String verbArg = "?verb=";

    protected String url;
    protected boolean saveXml = false;
    protected String dataPath;

    public OaiPmhImporterImpl(String url) {
        super();
        this.url = url;

        // http://hw.oeaw.ac.at/oai returns no character set in the header
        // Spring would use the default character set ISO_8859_1 in this case,
        // although the xml response itself defines UTF-8
        // Therefore we are using an explicit StringHttpMessageConverter with default UTF-8
        StringHttpMessageConverter messageConverter = new StringHttpMessageConverter();
        messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        restTemplate.setMessageConverters(Collections.singletonList(messageConverter));
    }

    public boolean isSaveXml() {
        return saveXml;
    }

    public void setSaveXml(boolean saveXml) {
        this.saveXml = saveXml;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    @Override
    public IdentifyType identify() {
        String xmlContent = fetchXmlResponse(url + verbArg + verbListIdentify);
        JAXBElement<OAIPMHtype> response = unmarshal(xmlContent);
        return response.getValue().getIdentify();
    }

    @Override
    public List<MetadataFormatType> listMetadataFormats(String identifier) {
        String xmlContent = fetchXmlResponse(url + verbArg + verbListMetadataFormats + "&identifier=" + identifier);
        JAXBElement<OAIPMHtype> response = unmarshal(xmlContent);
        return response.getValue().getListMetadataFormats().getMetadataFormat();
    }

    @Override
    public List<SetType> listSets() {
        String xmlContent = fetchXmlResponse(url + verbArg + verbListSets);
        JAXBElement<OAIPMHtype> response = unmarshal(xmlContent);
        return response.getValue().getListSets().getSet();
    }

    /*
    https://stackoverflow.com/questions/33906069/jaxb-unmarshall-where-does-elementnsimpl-come-from
     */
    @Override
    public RecordType getRecord(String identifier, String metadataFormat) {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        urlBuilder.append(verbArg);
        urlBuilder.append(verbGetRecord);
        urlBuilder.append("&metadataPrefix=");
        urlBuilder.append(metadataFormat);
        urlBuilder.append("&identifier=");
        urlBuilder.append(identifier);
        String requestUrl = urlBuilder.toString();

        String xmlContent = fetchXmlResponse(requestUrl);
        JAXBElement<OAIPMHtype> response = unmarshal(xmlContent);
        if (response.getValue() != null && response.getValue().getGetRecord() != null) {
            return response.getValue().getGetRecord().getRecord();
        } else {
           return null;
        }
    }

    @Override
    public List<HeaderType> listIdentifiers(LocalDate from) {
        CollectAllResultsResponseHandler<HeaderType> responseHandler = new CollectAllResultsResponseHandler<>();
        fetchRequest(verbListIdentifiers, from, mdf_oai_dc, null, responseHandler);
        return responseHandler.getResults();
    }

    @Override
    public void fetchIdentifiers(LocalDate from, ResultResponseHandler<HeaderType> responseHandler) {
        fetchRequest(verbListIdentifiers, from, mdf_oai_dc, null, responseHandler);
    }

    @Override
    public List<RecordType> listRecords(LocalDate from, String metadataFormat, String setName) {
        CollectAllResultsResponseHandler<RecordType> responseHandler = new CollectAllResultsResponseHandler<>();
        fetchRequest(verbListRecords, from, metadataFormat, setName, responseHandler);
        return responseHandler.getResults();
    }

    @Override
    public List<RecordType> listFirstRecords(LocalDate from, String metadataFormat, String setName) {
        CollectFirstResultsResponseHandler<RecordType> responseHandler = new CollectFirstResultsResponseHandler<>();
        fetchRequest(verbListRecords, from, metadataFormat, setName, responseHandler);
        return responseHandler.getResults();
    }

    @Override
    public void fetchRecords(LocalDate from, String metadataFormat, String setName, ResultResponseHandler<RecordType> responseHandler) {
        fetchRequest(verbListRecords, from, metadataFormat, setName, responseHandler);
    }

    private <T> void fetchRequest(String verb, LocalDate from, String metadataFormat, String setName, ResultResponseHandler<T> responseHandler) {

        boolean resume = false;

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        urlBuilder.append(verbArg);
        urlBuilder.append(verb);

        if (!resume) {
            urlBuilder.append("&metadataPrefix=");
            urlBuilder.append(metadataFormat);
            if (from != null) {
                urlBuilder.append("&from=");
                urlBuilder.append(DateUtil.formatDateYYYYMMDD(from));
            }
            if (setName != null) {
                urlBuilder.append("&set=");
                urlBuilder.append(setName);
            }
        }

        String requestUrl = urlBuilder.toString();

        String path = dataPath + "\\" + nameFromUrl(url) + "%03d.xml";

        int count = 1;
        while (true) {
            String xmlContent = null;
            try {
                xmlContent = fetchXmlResponse(requestUrl);
            } catch (Exception e) {
                logger.error("url: {}\t{}", requestUrl, e.getMessage());
                throw new RuntimeException(e);
            }

            if (isSaveXml()) {
                FileSupport.writeStringToFile(String.format(path, count), xmlContent);
            }
            count++;

            JAXBElement<OAIPMHtype> response = null;
            try {
                response = unmarshal(clean(xmlContent, requestUrl));
            } catch (OaiPmhApiException e) {
                logger.warn("url: {}\t{}", requestUrl, e.getMessage());
                break;
            }

            if (response == null) {
                break;
            }

            ResumptionTokenType resumptionTokenType = null;
            switch (verb) {
                case verbListIdentifiers: {
                    ListIdentifiersType listIdentifiersType = response.getValue().getListIdentifiers();
                    if (listIdentifiersType != null && listIdentifiersType.getHeader() != null) {
                        List<T> results = (List<T>) listIdentifiersType.getHeader();
                        boolean stop = responseHandler.handleResponse(results);
                        if (stop) {
                            break;
                        }
                        resumptionTokenType = listIdentifiersType.getResumptionToken();
                    }
                }
                case verbListRecords: {
                    ListRecordsType listRecordsType = response.getValue().getListRecords();
                    if (listRecordsType != null && listRecordsType.getRecord() != null) {
                        List<T> results = (List<T>) listRecordsType.getRecord();
                        boolean stop = responseHandler.handleResponse(results);
                        if (stop) {
                            break;
                        }
                        resumptionTokenType = listRecordsType.getResumptionToken();
                    }
                }
            }
            if (resumptionTokenType != null && resumptionTokenType.getValue() != null && !resumptionTokenType.getValue().isEmpty()) {
                // there is additional data available
                String resumptionParam = "&resumptionToken=" + resumptionTokenType.getValue();
                // rebuild the URL with verb und resumption only
                requestUrl = url + verbArg + verb + resumptionParam;
            } else {
                break;
            }
        }

        responseHandler.onFinished();

    }

    protected String fetchXmlResponse(String requestUrl) {
        // RestTemplate encodes the URL, so we may not encode it ourself
        ResponseEntity<String> xmlResponse = restTemplate.exchange(
                requestUrl, HttpMethod.GET, null, String.class);

        return xmlResponse.getBody();
    }

    /*
     * transforms xmlContent to a Java object
     */
    private JAXBElement<OAIPMHtype> unmarshal(String xmlContent) {
        JAXBElement<OAIPMHtype> response = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(OAIPMHtype.class, OaiDcType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlContent);
            response = (JAXBElement<OAIPMHtype>) unmarshaller.unmarshal(reader);
            OAIPMHtype oaipmHtype = response.getValue();
            if (oaipmHtype == null) {
                throw new RuntimeException("no OAIPMHtype received in response");
            }
            List<OAIPMHerrorType> errors = oaipmHtype.getError();
            if (errors != null && !errors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (OAIPMHerrorType error : errors) {
                    sb.append(error.getValue());
                    sb.append("\r\n");
                }
                throw new OaiPmhApiException(sb.toString());
            }
        } catch (JAXBException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return response;
    }

    // Pattern to match invalid XML characters
    private static final Pattern INVALID_XML_CHARS = Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\u10000-\\u10FFFF]");

    private String clean(String contents, String requestUrl) {
        String result = INVALID_XML_CHARS.matcher(contents).replaceAll("");
        if (!result.equals(contents)) {
            logger.warn("removed invalid xml characters in contents of response from {}", requestUrl);
        }
        return result;
    }

    private static class CollectFirstResultsResponseHandler<T> implements ResultResponseHandler<T> {

        private final List<T> results = new ArrayList<>();

        @Override
        public boolean handleResponse(List<T> records) {
            results.addAll(records);
            return true;
        }

        public List<T> getResults() {
            return results;
        }
    }
}
