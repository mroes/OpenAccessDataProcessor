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

package at.roesel.oadataprocessor.model;

import java.util.*;

import static at.roesel.oadataprocessor.support.PublicationSupport.propsFrom;

/*
 * Publication model for exports
 */
public class PublicationFlat {

    public String id;

    public String doi;

    public int year;

    public String title;

    public List<Author> authors;

    private PublicationType type;
    private Publisher publisher;
    private Publisher mainPublisher;

    private Journal journal;

    public String coat;
    public String color;
    public String colorUpw;

    public Licence licence;

    public List<SourceReferenceFlat> sources;

    public String embargoTime;    // 0, if there is no embargo, otherwise number of months
    public String embargoSource;

    public String version;
    public String versionSource;

    public Costs costs;

    public String oaVersionLink;
    public String oaPlace;
    public String oaPlaceSource;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }


    public PublicationType getType() {
        return type;
    }

    public void setType(PublicationType type) {
        this.type = type;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Publisher getMainPublisher() {
        return mainPublisher;
    }

    public void setMainPublisher(Publisher mainPublisher) {
        this.mainPublisher = mainPublisher;
    }

    public Journal getJournal() {
        return journal;
    }

    public void setJournal(Journal journal) {
        this.journal = journal;
    }

    public String getCoat() {
        return coat;
    }

    public void setCoat(String coat) {
        this.coat = coat;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorUpw() {
        return colorUpw;
    }

    public void setColorUpw(String colorUpw) {
        this.colorUpw = colorUpw;
    }

    public Licence getLicence() {
        return licence;
    }

    public void setLicence(Licence licence) {
        this.licence = licence;
    }

    public List<SourceReferenceFlat> getSources() {
        return sources;
    }

    public void setSources(List<SourceReferenceFlat> sources) {
        this.sources = sources;
    }

    public String getEmbargoTime() {
        return embargoTime;
    }

    public void setEmbargoTime(String embargoTime) {
        this.embargoTime = embargoTime;
    }

    public String getEmbargoSource() {
        return embargoSource;
    }

    public void setEmbargoSource(String embargoSource) {
        this.embargoSource = embargoSource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionSource() {
        return versionSource;
    }

    public void setVersionSource(String versionSource) {
        this.versionSource = versionSource;
    }

    public Costs getCosts() {
        return costs;
    }

    public void setCosts(Costs costs) {
        this.costs = costs;
    }

    public String getOaVersionLink() {
        return oaVersionLink;
    }

    public void setOaVersionLink(String oaVersionLink) {
        this.oaVersionLink = oaVersionLink;
    }

    public String getOaPlace() {
        return oaPlace;
    }

    public void setOaPlace(String oaPlace) {
        this.oaPlace = oaPlace;
    }

    public String getOaPlaceSource() {
        return oaPlaceSource;
    }

    public void setOaPlaceSource(String oaPlaceSource) {
        this.oaPlaceSource = oaPlaceSource;
    }


    @Override
    public String toString() {
        return "PublicationFlat{" +
                "id='" + id + '\'' +
                ", doi='" + doi + '\'' +
                '}';
    }

    public static PublicationFlat from(Publication publication) {
        PublicationFlat pub = new PublicationFlat();
        pub.id = publication.getId();
        pub.doi = publication.getDoi();
        pub.title = publication.getTitle();
        pub.year = publication.getYear();
        pub.authors = new ArrayList<>();
        for (at.roesel.oadataprocessor.model.Author author : publication.getAuthors()) {
            pub.authors.add(Author.from(author));
        }

        PublicationType publicationType = new PublicationType();
        pub.setType(publicationType);
        publicationType.setId(publication.getPubtypeId());

        Licence licence = new Licence();
        pub.setLicence(licence);
        licence.setLicence(publication.getLicence());

        pub.coat = publication.getCoat();
        pub.color = publication.getColor();
        pub.colorUpw = publication.getColorUpw();

        Publisher publisher = new Publisher();
        publisher.id = publication.getPublisherId();
        pub.setPublisher(publisher);

        Publisher mainPublisher = new Publisher();
        mainPublisher.id = publication.getMainPublisherId();
        pub.setMainPublisher(mainPublisher);

        Journal journal = new Journal();
        pub.setJournal(journal);
        journal.setId(publication.getJournalId());
        journal.setIssn(publication.getIssn());

        pub.sources = new ArrayList<>();
        for (SourceReference ref : publication.getSources()) {
            pub.sources.add(SourceReferenceFlat.from(ref));
        }

        Costs costs = new Costs();
        pub.setCosts(costs);

        PublicationProps props = propsFrom(publication, false);
        if (props != null) {
            licence.setUrl(props.licenceUrl);
            licence.setSource(props.licenceSource);
            pub.version = props.version;
            pub.versionSource = props.versionSource;
            pub.oaVersionLink = props.oaVersionLink;
            pub.oaPlace = props.oaPlace;
            pub.oaPlaceSource = props.oaPlaceSource;
            pub.embargoTime = props.embargoTime;
            pub.embargoSource = props.embargoSource;
            costs.setAmount(props.costs);
            costs.setCurrency(props.costsCurrency);
            costs.setSource(props.costsSource);
        }
        return pub;
    }

    public boolean hasAuthor(String lastName, String firstName) {
        firstName = firstName.toLowerCase();
        if (authors != null && !authors.isEmpty()) {
            for (Author author : authors) {
                if (author.getLastName().equalsIgnoreCase(lastName)) {
                    if (author.getFirstName().toLowerCase().startsWith(firstName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<String> institutions() {
        List<String> result = new ArrayList<>();
        if (sources != null) {
            for (SourceReferenceFlat reference : sources) {
                result.add(reference.institutionId);
            }
        }
        return result;
    }

    public List<String> nativeIds() {
        List<String> result = new ArrayList<>();
        if (sources != null) {
            for (SourceReferenceFlat reference : sources) {
                result.add(reference.nativeId);
            }
        }
        return result;
    }

    public List<String> corresponding() {
        List<String> result = new ArrayList<>();
        if (sources != null) {
            for (SourceReferenceFlat reference : sources) {
                result.add(reference.corr? "1" : "0");
            }
        }
        return result;
    }

    public static class Author {

        private String firstName;
        private String lastName;
        private String orcid;
        private String role;
        private String organisation;
        public boolean corr;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getOrcid() {
            return orcid;
        }

        public void setOrcid(String orcid) {
            this.orcid = orcid;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getOrganisation() {
            return organisation;
        }

        public void setOrganisation(String organisation) {
            this.organisation = organisation;
        }

        public boolean isCorr() {
            return corr;
        }

        public void setCorr(boolean corr) {
            this.corr = corr;
        }

        public static Author from(at.roesel.oadataprocessor.model.Author author) {
            Author result = new Author();
            result.setLastName(author.getLastName());
            result.setFirstName(author.getFirstName());
            result.setOrcid(author.getOrcid());
            result.setRole(author.getRole());
            result.setOrganisation(author.getOrganisation());
            result.setCorr(author.isCorresponding());
            return result;
        }


    }

    public static class Publisher {
        private String id;
        private String name;
        private String wikidataId;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWikidataId() {
            return wikidataId;
        }

        public void setWikidataId(String wikidataId) {
            this.wikidataId = wikidataId;
        }
    }

    public static class Journal {
        private String id;
        private String title;
        private String wikidataId;

        public Set<String> issn;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getWikidataId() {
            return wikidataId;
        }

        public void setWikidataId(String wikidataId) {
            this.wikidataId = wikidataId;
        }

        public Set<String> getIssn() {
            return issn;
        }

        public void setIssn(Collection<String> issn) {
            this.issn = new HashSet<>();
            this.issn.addAll(issn);
        }
    }

    public static class PublicationType {
        private int id;
        private String coarId;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCoarId() {
            return coarId;
        }

        public void setCoarId(String coarId) {
            this.coarId = coarId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

     public static class Licence {
        private String licence;
        private String url;
        private String source;

        public String getLicence() {
            return licence;
        }

        public void setLicence(String licence) {
            this.licence = licence;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

         public String getSource() {
             return source;
         }

         public void setSource(String source) {
             this.source = source;
         }
     }

     public static class Costs {
         private String amount;
         public String currency;
         public String source;

         public String getAmount() {
             return amount;
         }

         public void setAmount(String amount) {
             this.amount = amount;
         }

         public String getCurrency() {
             return currency;
         }

         public void setCurrency(String currency) {
             this.currency = currency;
         }

         public String getSource() {
             return source;
         }

         public void setSource(String source) {
             this.source = source;
         }
     }

}
