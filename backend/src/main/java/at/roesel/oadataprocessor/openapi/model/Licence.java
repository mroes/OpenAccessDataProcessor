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
 * Licence
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class Licence {

  private String licence;

  private String url;

  private String source;

  public Licence licence(String licence) {
    this.licence = licence;
    return this;
  }

  /**
   * normalized licence
   * @return licence
  */
  
  @Schema(name = "licence", example = "CC BY", description = "normalized licence", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("licence")
  public String getLicence() {
    return licence;
  }

  public void setLicence(String licence) {
    this.licence = licence;
  }

  public Licence url(String url) {
    this.url = url;
    return this;
  }

  /**
   * LicenceUrl
   * @return url
  */
  
  @Schema(name = "url", description = "LicenceUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Licence source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Source of Licence
   * @return source
  */
  
  @Schema(name = "source", description = "Source of Licence", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source")
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Licence licence = (Licence) o;
    return Objects.equals(this.licence, licence.licence) &&
        Objects.equals(this.url, licence.url) &&
        Objects.equals(this.source, licence.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(licence, url, source);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Licence {\n");
    sb.append("    licence: ").append(toIndentedString(licence)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
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

