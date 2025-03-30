package at.roesel.oadataprocessor.openapi.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Author
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class Author {

  private String given;

  private String family;

  private String orcid;

  private Boolean corresponding;

  public Author given(String given) {
    this.given = given;
    return this;
  }

  /**
   * Given name
   * @return given
  */
  
  @Schema(name = "given", example = "John", description = "Given name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("given")
  public String getGiven() {
    return given;
  }

  public void setGiven(String given) {
    this.given = given;
  }

  public Author family(String family) {
    this.family = family;
    return this;
  }

  /**
   * Family name
   * @return family
  */
  
  @Schema(name = "family", example = "Talisker", description = "Family name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("family")
  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public Author orcid(String orcid) {
    this.orcid = orcid;
    return this;
  }

  /**
   * ORCID of author
   * @return orcid
  */
  
  @Schema(name = "orcid", example = "http://orcid.org/0000-0002-9999-9999", description = "ORCID of author", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orcid")
  public String getOrcid() {
    return orcid;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

  public Author corresponding(Boolean corresponding) {
    this.corresponding = corresponding;
    return this;
  }

  /**
   * true, if the author is a corresponding author
   * @return corresponding
  */
  
  @Schema(name = "corresponding", example = "true", description = "true, if the author is a corresponding author", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corresponding")
  public Boolean getCorresponding() {
    return corresponding;
  }

  public void setCorresponding(Boolean corresponding) {
    this.corresponding = corresponding;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Author author = (Author) o;
    return Objects.equals(this.given, author.given) &&
        Objects.equals(this.family, author.family) &&
        Objects.equals(this.orcid, author.orcid) &&
        Objects.equals(this.corresponding, author.corresponding);
  }

  @Override
  public int hashCode() {
    return Objects.hash(given, family, orcid, corresponding);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Author {\n");
    sb.append("    given: ").append(toIndentedString(given)).append("\n");
    sb.append("    family: ").append(toIndentedString(family)).append("\n");
    sb.append("    orcid: ").append(toIndentedString(orcid)).append("\n");
    sb.append("    corresponding: ").append(toIndentedString(corresponding)).append("\n");
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

