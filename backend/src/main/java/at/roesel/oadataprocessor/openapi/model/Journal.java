package at.roesel.oadataprocessor.openapi.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Journal
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class Journal {

  private String title;

  private String wikidataId;

  @Valid
  private List<String> issn = new ArrayList<>();

  private String eissn;

  public Journal title(String title) {
    this.title = title;
    return this;
  }

  /**
   * title of journal
   * @return title
  */
  
  @Schema(name = "title", example = "Applied Physics", description = "title of journal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Journal wikidataId(String wikidataId) {
    this.wikidataId = wikidataId;
    return this;
  }

  /**
   * wikidata Id
   * @return wikidataId
  */
  
  @Schema(name = "wikidataId", example = "https://www.wikidata.org/wiki/Q880582", description = "wikidata Id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("wikidataId")
  public String getWikidataId() {
    return wikidataId;
  }

  public void setWikidataId(String wikidataId) {
    this.wikidataId = wikidataId;
  }

  public Journal issn(List<String> issn) {
    this.issn = issn;
    return this;
  }

  public Journal addIssnItem(String issnItem) {
    if (this.issn == null) {
      this.issn = new ArrayList<>();
    }
    this.issn.add(issnItem);
    return this;
  }

  /**
   * ISSNs of journal
   * @return issn
  */
  
  @Schema(name = "issn", example = "[\"1111-2222\"]", description = "ISSNs of journal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("issn")
  public List<String> getIssn() {
    return issn;
  }

  public void setIssn(List<String> issn) {
    this.issn = issn;
  }

  public Journal eissn(String eissn) {
    this.eissn = eissn;
    return this;
  }

  /**
   * Get eissn
   * @return eissn
  */
  
  @Schema(name = "eissn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("eissn")
  public String getEissn() {
    return eissn;
  }

  public void setEissn(String eissn) {
    this.eissn = eissn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Journal journal = (Journal) o;
    return Objects.equals(this.title, journal.title) &&
        Objects.equals(this.wikidataId, journal.wikidataId) &&
        Objects.equals(this.issn, journal.issn) &&
        Objects.equals(this.eissn, journal.eissn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, wikidataId, issn, eissn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Journal {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    wikidataId: ").append(toIndentedString(wikidataId)).append("\n");
    sb.append("    issn: ").append(toIndentedString(issn)).append("\n");
    sb.append("    eissn: ").append(toIndentedString(eissn)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

