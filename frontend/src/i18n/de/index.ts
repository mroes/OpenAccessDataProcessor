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
  developed_by: 'developed by',
  api_link: 'API',
  impress: 'Impressum',

  index_header: 'Herzlich Willkommen beim Austrian Datahub for Open Access Negotiations and Monitoring!',
  index_text1: 'Der Austrian Datahub for Open Access Negotiations and Monitoring bereitet Publikationsdaten aus unterschiedlichen Quellen auf, um sie für ein österreichweites Open Access-Monitoring und zur Unterstützung von Verhandlungen mit wissenschaftlichen Verlagen nutzen zu können. Ursprünglich im Projekt AT2OA2 entwickelt, eignet sich dieser Datahub für die Visualisierung des Open Access-Fortschritts bei wissenschaftlichen Publikationen in Österreich. Die Publikationsmetadaten werden aus den Forschungsinformationssystemen der beteiligten Institutionen eingespeist bzw. manuell von den Institutionen bereitgestellt.',

  index_piechart_text: 'Das nebenstehende Tortendiagramm zeigt den relativen Anteil an *Closed*, *Gold*, *Hybrid* und *Green* (und *Unknown*, wenn der Regler betätigt wurde) Publikationen über den gewählten Zeitraum, entweder in ihrer Gesamtheit (“alle Institutionen") oder von der ausgewählten Institution.',
  index_barchart_text: 'Das Säulendiagramm zeigt die absolute Anzahl der Publikationen pro Jahr bzw. Zeitraum an. Die jeweils linke Säule beinhaltet die *Gold*, *Hybrid* und *Green* Publikationen, die rechte, blaue Säule die Anzahl der *Closed*-Publikationen.',

  insights_text1: 'Diese Seite gewährt einen genauen Einblick in die Publikationsdaten der teilnehmenden Einrichtungen. Unterschiedliche Auswertungen und Analysen können angezeigt werden.\n\n' +
    'Das Betätigen des Reglers \'Prozentuelle Darstellung\' ermöglicht eine Darstellung der prozentualen Verteilung der verschiedenen OA-Arten.  Um das Verhältnis der Publikationen, deren OA-Status nicht bekannt ist (*Unknown*), zum Rest der Publikationen darzustellen, muss der Regler \'Unbekannten OA-Status einbeziehen\' ausgewählt werden.',
  coat_text1: 'Ist bei einer Publikation eine DOI vorhanden, ist eine detaillierte Darstellung der COAT-Klassifikation und das Nachvollziehen des OA-Status der gesuchten Publikation möglich.',

  publications_text1: 'Die Seite ermöglicht die gezielte Suche nach Publikationen, wobei verschiedene Filter wie Autor:innen\n' +
    'oder Institution verwendet werden können. Ein Download der Ergebnisse im .xlsx-Format ist ebenfalls möglich.',

  txt_asOfDate: 'Mit Stand 09.09.2024 sind das:',

  txt_about: 'Der Austrian Datahub for Open Access Negotiations and Monitoring bereitet Publikationsdaten aus unterschiedlichen [Quellen](/faq/11) (z.B. FIS/CRIS-Systeme) auf, um sie sowohl für ein österreichweites Open Access-Monitoring, als auch zur Unterstützung von Verhandlungen mit wissenschaftlichen Verlagen nutzen zu können.\n' +
    '\n\n' +
    'Dieser Datahub ist im Rahmen des vom Bundesministerium für Bildung, Wissenschaft und Forschung (BMBWF) geförderten Projekts Austrian Transition to Open Access [AT2OA](https://at2oa.at) (2017-2020) konzipiert\n' +
    'und während des Folgeprojekts AT2OA² (2021-2024) (Förderschiene ["Vorhaben zur digitalen und sozialen Transformation in der Hochschulbildung"](https://pubshop.bmbwf.gv.at/index.php?article_id=9&sort=title&search%5Btext%5D=digitalisierungsvorhaben&pub=799) erstellt und bearbeitet worden. Das Projekt AT2OA², dessen Projektlead die Universität Wien innehatte, verfolgte das vorrangige Ziel,\n' +
    'die Transformation von Closed zu Open Access (OA) bei wissenschaftlichen Publikationen voranzutreiben. Das Teilprojekt Datahub wurde von Patrick Danowski (ISTA)  und  Anna Hikl (BOKU University) geleitet.\n' +
    '\n\n' +
    'Alle in den Projekten AT2OA und AT2OA² veröffentlichten Publikationen, Richtlinien sowie andere Materialien finden sich auf der [Projektwebseite](https://at2oa.at) und in der entsprechenden [Zenodo-Community](https://zenodo.org/communities/at2oa2/records?q=&l=list&p=1&s=10&sort=newest) des Projekts.\n' +
    'Die im Datahub verarbeiteten Publikationsmetadaten stammen hauptsächlich aus den jeweiligen Forschungsinformationssystemen (FIS/CRIS) der beteiligten Institutionen.',

  txt_upload: 'Bitte geben Sie die Excel-Datei mit den Publikationen an, die Sie importieren möchten.\n\n' +
    'Die Datei muss im [Standardformat](https://oamonitor.obvsg.at/static/Import_Datenformat.pdf) aufgebaut sein.\n' +
    'Der Dateiname muss mit der ROR-ID (z.B. 03prydq77 für die Universität Wien) gefolgt von einem Unterstrich beginnen. Der Name nach dem Unterstrich ist beliebig.',

  publication: 'Publikation',
  publications: 'Publikationen',
  title_pub_distribution: 'Verteilung der Publikationen',
  publications_per_year: 'Publikationen pro Jahr',
  failed: 'Aktion fehlgeschlagen',
  success: 'Aktion war erfolgreich',
  classification_publication: 'COAT Klassifizierung einer Publikation',
  colors_per_institution: 'OpenAccess Farben per Institution',
  classification_publication_details: 'Ablauf der Klassifizierung',

  menu_home: 'Home',
  menu_insights: 'Details',
  menu_coat_classify: 'COAT Klassifizierung',
  menu_publications: 'Publikationen',
  menu_publishers: 'Verlage',
  menu_admin: 'Administration',
  menu_self_service: 'Daten-Upload',
  menu_faq: 'F.A.Q',
  menu_about: 'Über den Datahub',

  title_faq: 'Fragen & Antworten',
  title_update_publications: 'Upload von Publikationen',
  title_about: 'Über den Datahub',
  title_download_options: 'Download Einstellungen',

  doi: 'DOI',
  search: 'Suche',
  collectPubs: 'Start einer Aktualisierung',
  ingest: 'Start einer Verarbeitung',
  classify: 'COAT Klassifizierung der Publikationen',
  fetchPubs: 'Publikationen von den Repositorien holen',
  updatePubs: 'Publikationen aktualisieren',
  identifyJournals: 'Journale identifizieren',
  updatePublishers: 'Verlage von Wikidata importieren',
  identifyPublishers: 'Verlage identifizieren',
  createElasticIndex: 'Elasticsearch Index anlegen',
  updateElasticData: 'Elasticsearch data aktualisieren',
  resetUnknownPublisherFlag: 'Status unbekannter Verlag zurücksetzen',
  clearCache: 'Cache löschen',
  commandInput: 'Command',

  required: 'Eingabe ist erforderlich',
  checkDoi: 'Ungültige Doi',
  err_doi_not_in_db: 'DOI ist nicht in der Datenbank vorhanden',
  err_doi_not_found: 'Es wurden keine Daten gefunden',

  coatplace: 'place',
  coatlicence: 'licence',
  coatversion: 'version',
  coatembargo: 'embargo',
  coatconditions: 'conditions',


  criteria : 'Kriterium',
  coat_value: 'Wert',
  coat_description: 'Beschreibung',
  coat_source: 'Quelle',

  username: 'Benutzername',
  password: 'Passwort',

  btn_save: 'Speichern',
  btn_save_new: 'Speichern & Neu',
  btn_close: 'Schließen',
  btn_prev: 'Vorheriger',
  btn_next: 'Nächster',

  btn_cancel: 'Abbrechen',
  btn_login: 'Login',
  btn_logout: 'Abmelden',
  btn_signin: 'Anmelden',
  btn_update: 'Aktualisieren',
  btn_search: 'Suchen',
  btn_download: 'Herunterladen',
  btn_upload: 'Hochladen',
  btn_import_pubs: 'Publikationen übernehmen',
  btn_ingest_pubs: 'Publikationen übernehmen und verarbeiten',
  btn_new_upload: 'Weiteres Hochladen',
  btn_execute: 'Ausführen',

  institution: 'Institution',
  institutions: 'Institutionen',
  allinstitutions: 'Alle Institutionen',

  showUnknown: 'Unbekannten OA-Status einbeziehen',
  tt_showUnknown: 'Publikationen mit einem unbekannten OpenAccess-Status einbeziehen.',
  reducedColors: 'Reduzierte OA-Aufstellung',
  tt_reducedColors: 'Die Publikationen werden in weniger OpenAccess-Farben eingeteilt.',
  stackPercent: 'Prozentuelle Darstellung',
  tt_stackPercent: 'Die Daten in den Balken werden auf 100% normiert.',
  singleYear: 'Einzeljahr',
  tt_yearRange: 'Datumsbereich aktivieren',
  verticalBars: 'Senkrechte Balken',
  tt_verticalBars: 'Die Balken werden senkrecht dargestellt.',
  correspondingInstitution: 'Filterung auf Korrespondenzautor:in',
  tt_correspondingInstitution: 'Eine Publikation wird nur bei der Institution gezählt, wenn ein:e Korrespondenzautor:in zur Institution gehört.',

  tt_filterMainPublisher: 'Filter nach Hauptverlag',
  tt_includeUnknown: 'Publikationen mit unbekanntem OA-Status einbeziehen',

  title: 'Titel',
  author: 'Autor:in',
  year: 'Jahr',
  pubtype: 'Art',
  publication_type: 'Publikationsart',
  publication_year: 'Publikationsjahr',
  authors: 'Autor:innen',
  coat: 'COAT',
  color: 'OA Farbe',
  colorUpw_s: 'OA Farbe Upw',
  colorUpw: 'OA Farbe Unpaywall',
  months: 'Monate',
  nativeid: 'Pub. ID der Inst.',
  fields: 'Felder',
  placeholder_author: 'Familienname [Vorname]',

  multiple_pub_rows: 'Eine Ausgabezeile pro Institution',
  tt_multiple_pub_rows: 'Ausgabe von mehreren Zeilen, wenn eine Publikation von mehreren Institutionen geliefert wurde',
  publicationid: 'ID der Publikation',
  tt_publicationid: 'Interne ID der Publikation im Datahub',
  pubid_inst: 'ID der Publikation von der Institution',
  tt_pubid_inst: 'ID der Publikation von der Institution, die die Publikation geliefert hat',
  metadatasources: 'Quellangaben für Lizenz, Kosten und Embargo',
  tt_metadatasources: 'Angabe der Quellen für die Daten Lizenz, Kosten und Embargo',

  tt_authors: 'Angabe der Autor:innen',

  optional_fields: 'Optionale Angaben',

  publisher: 'Verlag',
  publishers: 'Verlage',
  mainpublisher: 'Hauptverlag',
  parentpublisher: 'Elternverlag',
  publisher_identified: 'Verlag (identifiziert)',
  allpublishers: 'Alle Verlage',

  journal: 'Zeitschrift',
  issn: 'ISSN',
  licence: 'Lizenz',
  licenceUrl: 'LizenzUrl',
  version: 'Version',
  costs: 'Kosten',
  oaPlace: 'Ort',
  oaUrl: 'Repository link',
  embargoTime: 'Embargo Frist',

  wikidata: 'WikiData',

  settings: 'Einstellungen',
  startyear: 'Von Jahr',
  endyear: 'Bis Jahr',
  exact: 'genau',

  id: 'ID',
  name: 'Name',
  auth_token: 'Autorisierung',

  article: 'Artikel',
  sources: 'Quellen',

  label_oa_per_year: 'OA pro Jahr',
  title_oa_per_year: 'Open Access Anteile pro Jahr',
  label_oa_per_publisher: 'OA pro Verlag',
  title_oa_per_publisher: 'Open Access Anteile pro Verlag',
  label_inst_per_year: 'Anteile Inst. pro Jahr',
  title_inst_per_year: 'Anteile der Institutionen pro Jahr',
  label_oa_per_publicationtype: 'OA pro Publikationsart',
  title_oa_per_publicationtype: 'Open Access Anteile pro Publikationsart',
//  label_oa_per_licence: 'OA pro Lizenz',
//  title_oa_per_licence: 'Open Access Anteile pro Lizenz',
  label_licence_per_oa: 'Lizenz pro OA',
  title_licence_per_oa: 'Lizenz pro Open Access Farbe',
  label_licence_per_year: 'Lizenz pro Jahr',
  title_licence_per_year: 'Lizenz pro Jahr',
  err_field_required: 'Eingabe ist erforderlich',
  err_min_date: 'Das Jahr muss nach {0} liegen',
  err_max_date: 'Das Jahr muss vor {0} liegen',
  err_min_after_max_date: '\'Von Jahr\' muss vor \'Bis Jahr\' liegen',
  err_max_before_min_date: '\'Bis Jahr\' muss nach \'Von Jahr\' liegen',

  err_invalid_file: 'Ungültige Datei',
//  err_missing_ror_prefix: 'Der Dateiname muss mit der ROR-ID der Institution, gefolgt von einem Unterstrich beginnen',
  err_missing_ror_prefix: 'Die ROR-ID aus dem Dateinamen wurde bei keiner beteiligten Institution gefunden',

  msg_results_are_restricted: '{0} Ergebnisse, {1} werden angezeigt.',
  msg_fetching_data: 'Daten werden geladen. Bitte etwas Geduld.',

  download_started: 'Der Download wird vorbereitet. Bitte etwas Geduld.',

  publication_filename: 'Datei mit Publikationen',

  labeL_pending: 'Ausstehend',
  lbl_information: 'Information',
  lbl_processingState: 'Verarbeitungsstatus',

  msg_choose_institution: 'Bitte wählen Sie eine Institution.',
  msg_enter_authtoken: 'Bitte geben Sie das Zugriffstoken ein',
  msg_choose_file: 'Bitte wählen Sie eine Datei',
  lbl_error: 'Fehler',
  msg_publications_imported: 'Die Publikationen wurden übernommen.',

  msg_please_login: 'Bitte melden Sie sich an',

  tt_search_mainpublisher: 'Filter nach dem Hauptverlag',

  lbl_fetch_result: 'Importierte Publikationen: {0}, neu: {1}, geändert: {2}, unverändert: {3}, ignoriert: {4}'
}
