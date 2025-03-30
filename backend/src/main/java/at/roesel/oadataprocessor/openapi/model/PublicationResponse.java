package at.roesel.oadataprocessor.openapi.model;

import java.net.URI;
import java.util.Objects;
import at.roesel.oadataprocessor.openapi.model.Error;
import at.roesel.oadataprocessor.openapi.model.Publication;
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
 * PublicationResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class PublicationResponse {

  private String cursor;

  @Valid
  private List<@Valid Publication> items = new ArrayList<>();

  private Error error;

  public PublicationResponse cursor(String cursor) {
    this.cursor = cursor;
    return this;
  }

  /**
   * search cursor
   * @return cursor
  */
  
  @Schema(name = "cursor", description = "search cursor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cursor")
  public String getCursor() {
    return cursor;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }

  public PublicationResponse items(List<@Valid Publication> items) {
    this.items = items;
    return this;
  }

  public PublicationResponse addItemsItem(Publication itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid Publication> getItems() {
    return items;
  }

  public void setItems(List<@Valid Publication> items) {
    this.items = items;
  }

  public PublicationResponse error(Error error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
  */
  @Valid 
  @Schema(name = "error", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PublicationResponse publicationResponse = (PublicationResponse) o;
    return Objects.equals(this.cursor, publicationResponse.cursor) &&
        Objects.equals(this.items, publicationResponse.items) &&
        Objects.equals(this.error, publicationResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cursor, items, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PublicationResponse {\n");
    sb.append("    cursor: ").append(toIndentedString(cursor)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

