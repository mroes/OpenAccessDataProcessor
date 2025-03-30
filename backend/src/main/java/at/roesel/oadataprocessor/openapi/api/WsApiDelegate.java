package at.roesel.oadataprocessor.openapi.api;

import at.roesel.oadataprocessor.openapi.model.Error;
import at.roesel.oadataprocessor.openapi.model.Publication;
import at.roesel.oadataprocessor.openapi.model.PublicationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link WsApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public interface WsApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /ws/publication/{publication_id} : API to retrieve a single publication by identifier
     *
     * @param publicationId Can either be - an internal publication id, e.g. 0008122f-1a4c-48ab-adb8-1947adf3854a - a DOI, e.g. 10.1016/j.jsc.2023.102236  (required)
     * @return publication metadata (status code 200)
     *         or error detail in case of Bad request (status code 400)
     *         or error if no publication is found (status code 404)
     *         or error detail in case of server error (status code 500)
     * @see WsApi#getPublication
     */
    default ResponseEntity<Publication> getPublication(String publicationId) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"error_description\", \"error\" : \"error\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"error_description\", \"error\" : \"error\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"error_description\" : \"error_description\", \"error\" : \"error\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /ws/publications : API to fetch publications by search criteria
     *
     * @param limit Number of records that will be returned in one request (optional, default to 200)
     * @param cursor Search cursor. Use the cursor to iterate through the publications (optional)
     * @param doi  (optional)
     * @param institution ROR-Id of requested institution, e.g. https://ror.org/03prydq77 (optional)
     * @param year year of publication (optional)
     * @param oacolor Open Access Color (optional)
     * @param include List of additional field names to include in the response - author  (optional)
     * @return publication metadata (status code 200)
     *         or error detail in case of Bad request (status code 400)
     *         or error detail in case of server error (status code 500)
     * @see WsApi#listPublications
     */
    default ResponseEntity<PublicationResponse> listPublications(Integer limit,
        String cursor,
        String doi,
        String institution,
        Integer year,
        String oacolor,
        List<String> include) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"cursor\" : \"cursor\", \"error\" : { \"error_description\" : \"error_description\", \"error\" : \"error\" }, \"items\" : [ { \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" }, { \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"cursor\" : \"cursor\", \"error\" : { \"error_description\" : \"error_description\", \"error\" : \"error\" }, \"items\" : [ { \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" }, { \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"cursor\" : \"cursor\", \"error\" : { \"error_description\" : \"error_description\", \"error\" : \"error\" }, \"items\" : [ { \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" }, { \"licence\" : { \"licence\" : \"CC BY\", \"source\" : \"source\", \"url\" : \"url\" }, \"costs\" : { \"amount\" : \"100\", \"currency\" : \"EUR\", \"source\" : \"source\" }, \"year\" : 2022, \"author\" : [ { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" }, { \"given\" : \"John\", \"corresponding\" : true, \"orcid\" : \"http://orcid.org/0000-0002-9999-9999\", \"family\" : \"Talisker\" } ], \"main_publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"title\" : \"Praktische Ansätze zur Verlagsnormierung\", \"type\" : { \"coarid\" : \"c_6501\", \"name\" : \"journal article\" }, \"version\" : \"publishedVersion\", \"coat\" : \"1,1,1,1,2\", \"repository_link\" : \"https://doi.org/10.1016/j.cma.2023.116491\", \"upw_color\" : \"gold\", \"journal\" : { \"issn\" : [ \"1111-2222\" ], \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"eissn\" : \"eissn\", \"title\" : \"Applied Physics\" }, \"oa_color\" : \"Gold-pur\", \"source_refs\" : [ { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" }, { \"institutionId\" : \"institutionId\", \"corresponding\" : true, \"nativeId\" : \"nativeId\" } ], \"publisher\" : { \"wikidataId\" : \"https://www.wikidata.org/wiki/Q880582\", \"name\" : \"Taylor and Francis\" }, \"id\" : \"273721\", \"place\" : \"publisher\", \"doi\" : \"10.9999/abcdef\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
