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
 * SourceRef
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class SourceRef {

  private String institutionId;

  private String nativeId;

  private Boolean corresponding;

  public SourceRef institutionId(String institutionId) {
    this.institutionId = institutionId;
    return this;
  }

  /**
   * Id of the institution that has the publication in the source repository
   * @return institutionId
  */
  
  @Schema(name = "institutionId", description = "Id of the institution that has the publication in the source repository", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("institutionId")
  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  public SourceRef nativeId(String nativeId) {
    this.nativeId = nativeId;
    return this;
  }

  /**
   * Id of the publication in the source repository of the institution
   * @return nativeId
  */
  
  @Schema(name = "nativeId", description = "Id of the publication in the source repository of the institution", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nativeId")
  public String getNativeId() {
    return nativeId;
  }

  public void setNativeId(String nativeId) {
    this.nativeId = nativeId;
  }

  public SourceRef corresponding(Boolean corresponding) {
    this.corresponding = corresponding;
    return this;
  }

  /**
   * true, if one of the corresponding authors is associated with the institution
   * @return corresponding
  */
  
  @Schema(name = "corresponding", example = "true", description = "true, if one of the corresponding authors is associated with the institution", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    SourceRef sourceRef = (SourceRef) o;
    return Objects.equals(this.institutionId, sourceRef.institutionId) &&
        Objects.equals(this.nativeId, sourceRef.nativeId) &&
        Objects.equals(this.corresponding, sourceRef.corresponding);
  }

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, nativeId, corresponding);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SourceRef {\n");
    sb.append("    institutionId: ").append(toIndentedString(institutionId)).append("\n");
    sb.append("    nativeId: ").append(toIndentedString(nativeId)).append("\n");
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

