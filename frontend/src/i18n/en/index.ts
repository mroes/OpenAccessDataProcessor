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

export default {
  application_title: 'Austrian Open Access Datahub',
  project_title: 'Austrian Transition to Open Access 2',
  api_link: 'API',
  impress: 'Impress',

  index_header: 'Welcome to the Austrian Datahub for Open Access Negotiations and Monitoring!',
  index_text1: 'The Austrian Datahub for Open Access Negotiations and Monitoring gathers publication data from various sources (e.g. CRIS systems) to enable Austria-wide open access monitoring and to support negotiations with scientific publishers. Originally developed as part of the AT2OA² project, this Datahub is designed to visualise the progress of Open Access in scientific publications in Austria. The publication data are either imported from the participating institutions\' current information systems or provided manually by the institutions themselves.',

  index_piechart_text: 'The pie chart shows the relative share of *closed*, *Gold*, *Hybrid* and *Green* (as well as *unknown* publications, if the toggle has been activated, either of “all institutions” or the selected institution.',
  index_barchart_text: 'The bar chart shows the absolute number of publications by year or period. The left bar represents *Gold*, *Hybrid* and *Green* publications, while the right blue bar shows the number of *Closed* publications.',

  insights_text1: 'This page provides a more detailed insight into the publication data of the participating institutions. Various evaluations and analyses can be displayed.\n\n' +
    'Clicking on the ‘Percentage representation’ toggle lets you see the percentage distribution of the different OA types. To display the ratio of publications with an *Unknown* status to the rest of the publications, the toggle “Include unknown OA status” must be activated.',

  coat_text1: 'If an article has a DOI, it is possible to obtain a detailed description of the COAT classification and to see how the publication\'s OA status was calculated.',

  publications_text1: 'You can search for publications on this page, applying various filters such as author or institution. It is also possible to download the data in .xlsx format.',

  txt_asOfDate: 'As of 09/09/2024, these are:',

  txt_about: 'The Austrian Datahub for Open Access Negotiations and Monitoring gathers publication data from various sources (e.g. CRIS systems) to enable Austria-wide open access monitoring and to support negotiations with scientific publishers.\n' +
    '\n\n' +
    'This Datahub was conceived as part of the  Austrian Transition to Open Access [AT2OA](https://at2oa.at) (2017-2020) project, funded by the Federal Ministry of Education, Science and Research (BMBWF),\n' +
    'and created and further developed during the follow-up project [AT2OA²](https://at2oa.at) (2021-2024) (funding programme ["Vorhaben zur digitalen und sozialen Transformation in der Hochschulbildung"](https://pubshop.bmbwf.gv.at/index.php?article_id=9&sort=title&search%5Btext%5D=digitalisierungsvorhaben&pub=799)). The project AT2OA², led by the University of Vienna,\n' +
    'primarily aimed to promote the transformation from Closed to Open Access (OA) in scientific publications. The Datahub subproject was led by Patrick Danowski (ISTA) and Anna Hikl (BOKU University).\n' +
    '\n\n' +
    'All publications, guidelines and other materials published as part of the projects AT2OA and AT2OA² can be found on the [project website](https://at2oa.at) and in the corresponding [Zenodo community](https://zenodo.org/communities/at2oa2/records?q=&l=list&p=1&s=10&sort=newest) of the project.\n' +
    'The publication metadata processed in the Datahub are obtained primarily from the current research information systems (CRIS) of the participating institutions.\n',

  txt_upload: 'Please provide the Excel file with the publications you want to import.\n\n' +
    'The file must follow the [standard format](https://oamonitor.obvsg.at/static/Import_Datenformat.pdf).\n' +
    'The filename must start with the ROR ID (e.g., 03prydq77 for the University of Vienna), followed by an underscore. The name after the underscore is arbitrary.',

  publication: 'Publication',
  publications: 'Publications',
  title_pub_distribution: 'Distribution of publications',
  publications_per_year: 'Publications by year',
  failed: 'Action failed',
  success: 'Action was successful',
  classification_publication: 'COAT classification of a publication',
  colors_per_institution: 'OpenAccess colors by institution',
  classification_publication_details: 'Classification flow',

  menu_home: 'Home',
  menu_insights: 'Insights',
  menu_coat_classify: 'COAT classification',
  menu_publications: 'Publications',
  menu_publishers: 'Publishers',
  menu_admin: 'Administration',
  menu_self_service: 'Upload of publications',
  menu_faq: 'F.A.Q',
  menu_about: 'About the Datahub',

  title_faq: 'Frequently asked questions',
  title_update_publications: 'Data upload',
  title_about: 'About the Datahub',
  title_download_options: 'Download settings',

  doi: 'DOI',
  search: 'Suche',
  classify: 'COAT Klassifizierung der Publikationen',
  collectPubs: 'Start einer Aktualisierung',
  ingest: 'Start einer Verarbeitung',
  fetchPubs: 'Publikationen von den Repositorien holen',
  updatePubs: 'Publikationen aktualisieren',
  identifyJournals: 'Journale identifizieren',
  updatePublishers: 'Verlage von Wikidata importieren',
  identifyPublishers: 'Verlage identifizieren',
  createElasticIndex: 'Elasticsearch Index anlegen',
  updateElasticData: 'Elasticsearch data aktualisieren',
  resetUnknownPublisherFlag: 'Reset status of unknown publisher',
  clearCache: 'Cache löschen',
  commandInput: 'Command',

  required: 'Input is required',
  checkDoi: 'Invalid DOI',
  err_doi_not_in_db: 'DOI ist nicht in der Datenbank vorhanden',
  err_doi_not_found: 'No data found',

  coatplace: 'place',
  coatlicence: 'licence',
  coatversion: 'version',
  coatembargo: 'embargo',
  coatconditions: 'conditions',

  criteria : 'Criterion',
  coat_value: 'Value',
  coat_description: 'Description',
  coat_source: 'Source',

  username: 'Username',
  password: 'Password',

  btn_save: 'Save',
  btn_save_new: 'Save & New',
  btn_close: 'Close',
  btn_prev: 'Previous',
  btn_next: 'Next',

  btn_cancel: 'Cancel',
  btn_login: 'Login',
  btn_logout: 'Logout',
  btn_signin: 'Sign in',
  btn_update: 'Update',
  btn_search: 'Search',
  btn_download: 'Download',
  btn_upload: 'Upload',
  btn_import_pubs: 'Import publications',
  btn_ingest_pubs: 'Import and ingest publications',
  btn_new_upload: 'Additional uploading',
  btn_execute: 'Execute',

  institution: 'Institution',
  institutions: 'Institutions',
  allinstitutions: 'All institutions',

  showUnknown: 'Include unknown OA status',
  tt_showUnknown: 'Include publications with an unknown OpenAccess status.',
  reducedColors: 'Reduced OA colors',
  tt_reducedColors: 'The publications are categorized into fewer Open Access colors',
  stackPercent: 'Percentage representation',
  tt_stackPercent: 'The data in the bars are normalized to 100%.',
  singleYear: 'Single year',
  tt_yearRange: 'Activate year range',
  verticalBars: 'Vertical bars',
  tt_verticalBars: 'The bars are displayed vertically.',
  correspondingInstitution: 'Filter on Corresponding Author',
  tt_correspondingInstitution: 'A publication is only counted for the institution if a corresponding author belongs to the institution.',

  tt_filterMainPublisher: 'Filter by main publisher',
  tt_includeUnknown: 'Include publications with unknown OA status',

  title: 'Title',
  author: 'Author',
  year: 'Year',
  pubtype: 'Type',
  publication_type: 'Type of publication',
  publication_year: 'Year of publication',
  authors: 'Authors',
  coat: 'COAT',
  color: 'OA Color',
  colorUpw_s: 'OA Color Upw',
  colorUpw: 'OA Color Unpaywall',
  months: 'months',
  nativeid: 'Pub. ID of inst.',
  fields: 'Fields',
  placeholder_author: 'Family name [First name]',

  multiple_pub_rows: 'One output row per institution',
  tt_multiple_pub_rows: 'Output of multiple rows if a publication was provided by multiple institutions',
  publicationid: 'ID of the publication',
  tt_publicationid: 'Internal ID of the publication in the Datahub',
  pubid_inst: 'ID of the publication from the institution',
  tt_pubid_inst: 'ID of the publication from the institution that provided the publication',
  metadatasources: 'Source information for license, costs, and embargo',
  tt_metadatasources: 'Source details for data on license, costs, and embargo',
  tt_authors: 'Output of the authors',

  optional_fields: 'Optional information',

  publisher: 'Publisher',
  publishers: 'Publishers',
  mainpublisher: 'Main publisher',
  parentpublisher: 'Parent publisher',
  publisher_identified: 'Publisher (identified)',
  allpublishers: 'All publishers',

  journal: 'Journal',
  issn: 'ISSN',
  licence: 'Licence',
  licenceUrl: 'LicenceUrl',
  version: 'Version',
  costs: 'Costs',
  oaPlace: 'Place',
  oaUrl: 'Repository link',
  embargoTime: 'Embargo Frist',

  wikidata: 'WikiData',

  settings: 'Einstellungen',
  startyear: 'From year',
  endyear: 'To year',
  exact: 'exact',

  id: 'ID',
  name: 'Name',
  auth_token: 'Authorization',

  article: 'Articles',
  sources: 'Sources',

  label_oa_per_year: 'OA per year',
  title_oa_per_year: 'Open Access shares per year',
  label_oa_per_publisher: 'OA per publisher',
  title_oa_per_publisher: 'Open Access shares per publisher',
  label_inst_per_year: 'Shares of the inst. per year',
  title_inst_per_year: 'Shares of the institutions per year',
  label_oa_per_publicationtype: 'OA per publication type',
  title_oa_per_publicationtype: 'Open Access shares per publication type',
//  label_oa_per_licence: 'OA pro Lizenz',
//  title_oa_per_licence: 'Open Access Anteile pro Lizenz',
  label_licence_per_oa: 'Licence per OA',
  title_licence_per_oa: 'Licence per Open Access colour',
  label_licence_per_year: 'Licence per year',
  title_licence_per_year: 'Licence per year',
  err_field_required: 'Input is required.',
  err_min_date: 'The year must be after {0}.',
  err_max_date: 'The year must be before {0}.',
  err_min_after_max_date: '\'From Year\' must be before \'To Year\'.',
  err_max_before_min_date: '\'To Year\' must be after \'From Year\'.',

  err_invalid_file: 'Invalid file',
  err_missing_ror_prefix: 'The filename must start with the institution\'s ROR ID, followed by an underscore',

  msg_results_are_restricted: '{0} results, {1} are displayed.',
  msg_fetching_data: 'Data is being loaded. Please be patient.',

  download_started: 'Preparing download. Please be patient.',

  publication_filename: 'File with publications',

  labeL_pending: 'pending',
  lbl_information: 'Information',
  lbl_processingState: 'Processing state',

  msg_choose_institution: 'Please choose an institution',
  msg_enter_authtoken: 'Please enter an authorization token',
  msg_choose_file: 'Please choose a file',
  lbl_error: 'Error',
  msg_publications_imported: 'The publications have been imported.',
  msg_please_login: 'Please login',

  tt_search_mainpublisher: 'Filter by the main publisher',

  lbl_fetch_result: 'Imported publication sources: {0}, created: {1}, modified: {2}, unchanged: {3}, ignored: {4}'

}
