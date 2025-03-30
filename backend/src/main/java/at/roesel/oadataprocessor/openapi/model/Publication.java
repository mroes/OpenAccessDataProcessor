package at.roesel.oadataprocessor.openapi.model;

import java.net.URI;
import java.util.Objects;
import at.roesel.oadataprocessor.openapi.model.Author;
import at.roesel.oadataprocessor.openapi.model.Costs;
import at.roesel.oadataprocessor.openapi.model.Journal;
import at.roesel.oadataprocessor.openapi.model.Licence;
import at.roesel.oadataprocessor.openapi.model.PublicationType;
import at.roesel.oadataprocessor.openapi.model.Publisher;
import at.roesel.oadataprocessor.openapi.model.SourceRef;
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
 * Publication
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-29T11:37:29.687691600+01:00[Europe/Vienna]", comments = "Generator version: 7.5.0")
public class Publication {

  private String id;

  private String doi;

  private String title;

  @Valid
  private List<@Valid Author> author = new ArrayList<>();

  private Integer year;

  private String coat;

  private String oaColor;

  private String upwColor;

  private PublicationType type;

  private Publisher publisher;

  private Publisher mainPublisher;

  private Journal journal;

  private Licence licence;

  private Costs costs;

  private String version;

  private String place;

  private String repositoryLink;

  @Valid
  private List<@Valid SourceRef> sourceRefs = new ArrayList<>();

  public Publication id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique internal Id of publication
   * @return id
  */
  
  @Schema(name = "id", example = "273721", description = "Unique internal Id of publication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Publication doi(String doi) {
    this.doi = doi;
    return this;
  }

  /**
   * DOI of publication
   * @return doi
  */
  
  @Schema(name = "doi", example = "10.9999/abcdef", description = "DOI of publication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doi")
  public String getDoi() {
    return doi;
  }

  public void setDoi(String doi) {
    this.doi = doi;
  }

  public Publication title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @Schema(name = "title", example = "Praktische Ansätze zur Verlagsnormierung", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Publication author(List<@Valid Author> author) {
    this.author = author;
    return this;
  }

  public Publication addAuthorItem(Author authorItem) {
    if (this.author == null) {
      this.author = new ArrayList<>();
    }
    this.author.add(authorItem);
    return this;
  }

  /**
   * Get author
   * @return author
  */
  @Valid 
  @Schema(name = "author", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("author")
  public List<@Valid Author> getAuthor() {
    return author;
  }

  public void setAuthor(List<@Valid Author> author) {
    this.author = author;
  }

  public Publication year(Integer year) {
    this.year = year;
    return this;
  }

  /**
   * year of publication
   * @return year
  */
  
  @Schema(name = "year", example = "2022", description = "year of publication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("year")
  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public Publication coat(String coat) {
    this.coat = coat;
    return this;
  }

  /**
   * [COAT of publication](https://doi.org/10.31263/voebm.v72i1.2276) 
   * @return coat
  */
  
  @Schema(name = "coat", example = "1,1,1,1,2", description = "[COAT of publication](https://doi.org/10.31263/voebm.v72i1.2276) ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("coat")
  public String getCoat() {
    return coat;
  }

  public void setCoat(String coat) {
    this.coat = coat;
  }

  public Publication oaColor(String oaColor) {
    this.oaColor = oaColor;
    return this;
  }

  /**
   * Open Access Color of publication
   * @return oaColor
  */
  
  @Schema(name = "oa_color", example = "Gold-pur", description = "Open Access Color of publication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("oa_color")
  public String getOaColor() {
    return oaColor;
  }

  public void setOaColor(String oaColor) {
    this.oaColor = oaColor;
  }

  public Publication upwColor(String upwColor) {
    this.upwColor = upwColor;
    return this;
  }

  /**
   * Open Access Color of publication from Unpaywall
   * @return upwColor
  */
  
  @Schema(name = "upw_color", example = "gold", description = "Open Access Color of publication from Unpaywall", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upw_color")
  public String getUpwColor() {
    return upwColor;
  }

  public void setUpwColor(String upwColor) {
    this.upwColor = upwColor;
  }

  public Publication type(PublicationType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @Valid 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public PublicationType getType() {
    return type;
  }

  public void setType(PublicationType type) {
    this.type = type;
  }

  public Publication publisher(Publisher publisher) {
    this.publisher = publisher;
    return this;
  }

  /**
   * Get publisher
   * @return publisher
  */
  @Valid 
  @Schema(name = "publisher", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("publisher")
  public Publisher getPublisher() {
    return publisher;
  }

  public void setPublisher(Publisher publisher) {
    this.publisher = publisher;
  }

  public Publication mainPublisher(Publisher mainPublisher) {
    this.mainPublisher = mainPublisher;
    return this;
  }

  /**
   * Get mainPublisher
   * @return mainPublisher
  */
  @Valid 
  @Schema(name = "main_publisher", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("main_publisher")
  public Publisher getMainPublisher() {
    return mainPublisher;
  }

  public void setMainPublisher(Publisher mainPublisher) {
    this.mainPublisher = mainPublisher;
  }

  public Publication journal(Journal journal) {
    this.journal = journal;
    return this;
  }

  /**
   * Get journal
   * @return journal
  */
  @Valid 
  @Schema(name = "journal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("journal")
  public Journal getJournal() {
    return journal;
  }

  public void setJournal(Journal journal) {
    this.journal = journal;
  }

  public Publication licence(Licence licence) {
    this.licence = licence;
    return this;
  }

  /**
   * Get licence
   * @return licence
  */
  @Valid 
  @Schema(name = "licence", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("licence")
  public Licence getLicence() {
    return licence;
  }

  public void setLicence(Licence licence) {
    this.licence = licence;
  }

  public Publication costs(Costs costs) {
    this.costs = costs;
    return this;
  }

  /**
   * Get costs
   * @return costs
  */
  @Valid 
  @Schema(name = "costs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("costs")
  public Costs getCosts() {
    return costs;
  }

  public void setCosts(Costs costs) {
    this.costs = costs;
  }

  public Publication version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Open Access version
   * @return version
  */
  
  @Schema(name = "version", example = "publishedVersion", description = "Open Access version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Publication place(String place) {
    this.place = place;
    return this;
  }

  /**
   * Open Access place
   * @return place
  */
  
  @Schema(name = "place", example = "publisher", description = "Open Access place", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("place")
  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  public Publication repositoryLink(String repositoryLink) {
    this.repositoryLink = repositoryLink;
    return this;
  }

  /**
   * Open Access repository link
   * @return repositoryLink
  */
  
  @Schema(name = "repository_link", example = "https://doi.org/10.1016/j.cma.2023.116491", description = "Open Access repository link", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("repository_link")
  public String getRepositoryLink() {
    return repositoryLink;
  }

  public void setRepositoryLink(String repositoryLink) {
    this.repositoryLink = repositoryLink;
  }

  public Publication sourceRefs(List<@Valid SourceRef> sourceRefs) {
    this.sourceRefs = sourceRefs;
    return this;
  }

  public Publication addSourceRefsItem(SourceRef sourceRefsItem) {
    if (this.sourceRefs == null) {
      this.sourceRefs = new ArrayList<>();
    }
    this.sourceRefs.add(sourceRefsItem);
    return this;
  }

  /**
   * Get sourceRefs
   * @return sourceRefs
  */
  @Valid 
  @Schema(name = "source_refs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("source_refs")
  public List<@Valid SourceRef> getSourceRefs() {
    return sourceRefs;
  }

  public void setSourceRefs(List<@Valid SourceRef> sourceRefs) {
    this.sourceRefs = sourceRefs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Publication publication = (Publication) o;
    return Objects.equals(this.id, publication.id) &&
        Objects.equals(this.doi, publication.doi) &&
        Objects.equals(this.title, publication.title) &&
        Objects.equals(this.author, publication.author) &&
        Objects.equals(this.year, publication.year) &&
        Objects.equals(this.coat, publication.coat) &&
        Objects.equals(this.oaColor, publication.oaColor) &&
        Objects.equals(this.upwColor, publication.upwColor) &&
        Objects.equals(this.type, publication.type) &&
        Objects.equals(this.publisher, publication.publisher) &&
        Objects.equals(this.mainPublisher, publication.mainPublisher) &&
        Objects.equals(this.journal, publication.journal) &&
        Objects.equals(this.licence, publication.licence) &&
        Objects.equals(this.costs, publication.costs) &&
        Objects.equals(this.version, publication.version) &&
        Objects.equals(this.place, publication.place) &&
        Objects.equals(this.repositoryLink, publication.repositoryLink) &&
        Objects.equals(this.sourceRefs, publication.sourceRefs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, doi, title, author, year, coat, oaColor, upwColor, type, publisher, mainPublisher, journal, licence, costs, version, place, repositoryLink, sourceRefs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Publication {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    doi: ").append(toIndentedString(doi)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
    sb.append("    coat: ").append(toIndentedString(coat)).append("\n");
    sb.append("    oaColor: ").append(toIndentedString(oaColor)).append("\n");
    sb.append("    upwColor: ").append(toIndentedString(upwColor)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    publisher: ").append(toIndentedString(publisher)).append("\n");
    sb.append("    mainPublisher: ").append(toIndentedString(mainPublisher)).append("\n");
    sb.append("    journal: ").append(toIndentedString(journal)).append("\n");
    sb.append("    licence: ").append(toIndentedString(licence)).append("\n");
    sb.append("    costs: ").append(toIndentedString(costs)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    place: ").append(toIndentedString(place)).append("\n");
    sb.append("    repositoryLink: ").append(toIndentedString(repositoryLink)).append("\n");
    sb.append("    sourceRefs: ").append(toIndentedString(sourceRefs)).append("\n");
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

