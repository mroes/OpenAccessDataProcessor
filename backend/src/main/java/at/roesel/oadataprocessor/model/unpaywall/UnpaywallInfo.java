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

package at.roesel.oadataprocessor.model.unpaywall;

import static at.roesel.common.BeanSupport.buildString;

public class UnpaywallInfo {

    public String doi;
    public String doi_url;
    public String title;
    public String published_date;
    public String journal_name;
    public String journal_issns;
//    public String journal_issn_l;
    public boolean journal_is_oa;
    public boolean journal_is_in_doaj;
    public String publisher;
    public boolean is_oa;
    public String oa_status;
    public String best_oa_location;
    public int oa_locations;
    public String authors;

    public static UnpaywallInfo build(UnpaywallResource resource) {
        UnpaywallInfo info = new UnpaywallInfo();
        info.doi = resource.doi;
        info.doi_url = resource.doi_url;
        info.title = resource.title;
        info.published_date = resource.published_date;
        info.journal_name = resource.journal_name;
        info.journal_issns = resource.journal_issns;
//        info.journal_issn_l = resource.journal_issn_l;
        info.journal_is_oa = resource.journal_is_oa;
        info.journal_is_in_doaj = resource.journal_is_in_doaj;
        info.publisher = resource.publisher;
        info.is_oa = resource.is_oa;
        info.oa_status = resource.oa_status;

        info.best_oa_location = buildLocation(resource.best_oa_location);
        if (resource.oa_locations != null) {
            info.oa_locations = resource.oa_locations.size();
        }

        info.authors = buildString(resource.zAuthors, author -> {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(author.given);
            builder.append("; ");
            builder.append(author.family);
            if (author.sequence != null) {
                builder.append("; ");
                builder.append(author.sequence);
            }
            if (author.orcid != null) {
                builder.append("; ");
                builder.append(author.orcid);
            }
            if (author.authenticatedOrcid != null) {
                builder.append("; ");
                builder.append(author.authenticatedOrcid);
            }
            builder.append(" }");
            return builder.toString();
        });

        return info;
    }

    private static String buildLocation(UpwOaLocation location) {
        if (location == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{ ");
        builder.append(location.version);
        builder.append("; ");
        builder.append(location.host_type);
        builder.append("; ");
        builder.append(location.url);
        builder.append("; ");
        builder.append(location.url_for_landing_page);
        builder.append("; ");
        if (location.url_for_pdf != null) {
            builder.append(location.url_for_pdf);
        }
        builder.append("; ");
        if (location.evidence != null) {
            builder.append(location.evidence);
        }
        builder.append("; ");
        if (location.licence != null) {
            builder.append(location.licence);
        }
        builder.append("; ");
        if (location.oa_date != null) {
            builder.append(location.oa_date);
        }
        builder.append(" }");
        return builder.toString();
    }

    public String getDoi() {
        return doi;
    }

    public String getDoi_url() {
        return doi_url;
    }

    public String getTitle() {
        return title;
    }

    public String getPublished_date() {
        return published_date;
    }

    public String getJournal_name() {
        return journal_name;
    }

    public String getJournal_issns() {
        return journal_issns;
    }

    /*
    public String getJournal_issn_l() {
        return journal_issn_l;
    }
     */

    public boolean isJournal_is_oa() {
        return journal_is_oa;
    }

    public boolean isJournal_is_in_doaj() {
        return journal_is_in_doaj;
    }

    public String getPublisher() {
        return publisher;
    }

    public boolean isIs_oa() {
        return is_oa;
    }

    public String getOa_status() {
        return oa_status;
    }

    public String getBest_oa_location() {
        return best_oa_location;
    }

    public int getOa_locations() {
        return oa_locations;
    }

    public String getAuthors() {
        return authors;
    }
}
