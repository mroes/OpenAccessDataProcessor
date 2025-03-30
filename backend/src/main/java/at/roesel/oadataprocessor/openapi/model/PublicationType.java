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
 * PublicationType
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class PublicationType {

  private String coarid;

  private String name;

  public PublicationType coarid(String coarid) {
    this.coarid = coarid;
    return this;
  }

  /**
   * COAR Id
   * @return coarid
  */
  
  @Schema(name = "coarid", example = "c_6501", description = "COAR Id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("coarid")
  public String getCoarid() {
    return coarid;
  }

  public void setCoarid(String coarid) {
    this.coarid = coarid;
  }

  public PublicationType name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of type
   * @return name
  */
  
  @Schema(name = "name", example = "journal article", description = "Name of type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PublicationType publicationType = (PublicationType) o;
    return Objects.equals(this.coarid, publicationType.coarid) &&
        Objects.equals(this.name, publicationType.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coarid, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PublicationType {\n");
    sb.append("    coarid: ").append(toIndentedString(coarid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

