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

package at.roesel.oadataprocessor.config;

import at.roesel.common.FieldEncryptor;
import at.roesel.common.NullFieldEncryptor;
import at.roesel.oadataprocessor.model.OAColor;
import org.jasypt.util.text.TextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "oamonitor")
public class AppSettings implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(AppSettings.class);

    // contact eMail, used for API services
    @Value("${contact.email}")
    private String contactEMail;

    // In quotation marks to prevent special characters like & from being interpreted.
    @Value("${fieldEncryptKey}")
    private String fieldEncryptKey;

    @Value("${downloadTimeout:300000}")
    private long downloadTimeout;

    @Value("${apikey.sherpa}")
    private String apiKeySherpa;

    private TextEncryptor fieldEncryptor;

    @Value("${elastic.publication_index}")
    private String elasticPublicationIndex;

    @Value("${data.path}")
    private String dataPath;

    @Value("${upload.path:dummy}")
    private String uploadPath;

    @Value("#{'${oacolors}'.split(',')}")
    private List<String> openaccessColorsNames;

    @Value("#{'${oacolors.hex}'.split(',')}")
    private List<String> openaccessColorsHex;

    private List<OAColor> openaccessColors;

    @Value("#{'${oacolorsreduced}'.split(',')}")
    private List<String> openaccessColorsReducedNames;

    @Value("#{'${oacolorsreduced.hex}'.split(',')}")
    private List<String> openaccessColorsReducedHex;

    private List<OAColor> openaccessColorsReduced;

    @Value("${cron.publications.update}")
    private String updatePublicationsCronExpression;

    @Value("${cron.openapc.update}")
    private String updateOpenApcCronExpression;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getContactEMail() {
        return contactEMail;
    }

    public void setContactEMail(String contactEMail) {
        this.contactEMail = contactEMail;
    }

    public String getApiKeySherpa() {
        return apiKeySherpa;
    }

    public void setApiKeySherpa(String apiKeySherpa) {
        this.apiKeySherpa = apiKeySherpa;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public List<OAColor> getOpenaccessColors() {
        return openaccessColors;
    }

    public List<OAColor> getOpenaccessColorsReduced() {
        return openaccessColorsReduced;
    }

    public TextEncryptor fieldEncryptor() {
        return fieldEncryptor;
    }

    public String getElasticPublicationIndex() {
        return elasticPublicationIndex;
    }

    public void setElasticPublicationIndex(String elasticPublicationIndex) {
        this.elasticPublicationIndex = elasticPublicationIndex;
    }

    public long getDownloadTimeout() {
        return downloadTimeout;
    }

    public String getUpdatePublicationsCronExpression() {
        return updatePublicationsCronExpression;
    }

    public void setUpdatePublicationsCronExpression(String updatePublicationsCronExpression) {
        this.updatePublicationsCronExpression = updatePublicationsCronExpression;
    }

    public String getUpdateOpenApcCronExpression() {
        return updateOpenApcCronExpression;
    }

    public void setUpdateOpenApcCronExpression(String updateOpenApcCronExpression) {
        this.updateOpenApcCronExpression = updateOpenApcCronExpression;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeFieldEncryptor();
        initializeOaColors();
    }


    private void initializeFieldEncryptor() {
        String key = null;
        if (fieldEncryptKey != null && !fieldEncryptKey.isEmpty()) {
            key = decodeBase64(fieldEncryptKey);
            if (key != null) {
                fieldEncryptor = new FieldEncryptor(key);
            }
        }
        if (key == null) {
            logger.warn("FieldEncryptKey not present. Falling back to no encryption");
            fieldEncryptor = new NullFieldEncryptor();
        }
        // Set the key to null to avoid unnecessary storage
        fieldEncryptKey = null;
    }

    private static String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        // check if the bytes are valid
        for (byte b : decodedBytes) {
            if (b < 0) {
                return null;
            }
        }
        String decodedString = new String(decodedBytes);
        return decodedString;
    }

    private void initializeOaColors() {
        // extended color scheme
        openaccessColors = buildColors(openaccessColorsNames, openaccessColorsHex);
        // reduced color scheme
        openaccessColorsReduced = buildColors(openaccessColorsReducedNames, openaccessColorsReducedHex);
    }

    private List<OAColor> buildColors(List<String> colorsName, List<String> colorsHex) {
        List<OAColor> colors = new ArrayList<>();
        for (int i = 0; i < colorsName.size(); i++) {
            String colorHex;
            if (i < colorsHex.size()) {
                colorHex = colorsHex.get(i);
            } else {
                colorHex = "";
            }
            OAColor color = new OAColor(colorsName.get(i), colorHex);
            colors.add(color);
        }
        return colors;
    }

}
