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
 * Publisher
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class Publisher {

  private String name;

  private String wikidataId;

  public Publisher name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  
  @Schema(name = "name", example = "Taylor and Francis", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Publisher wikidataId(String wikidataId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Publisher publisher = (Publisher) o;
    return Objects.equals(this.name, publisher.name) &&
        Objects.equals(this.wikidataId, publisher.wikidataId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, wikidataId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Publisher {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    wikidataId: ").append(toIndentedString(wikidataId)).append("\n");
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

