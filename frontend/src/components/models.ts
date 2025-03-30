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

export interface ChartSettings {
  showUnknown: boolean,
  startYear: number,
  endYear: number,
}

export interface MinMax {
  min: number,
  max: number
}

export interface SelectionFilterOptions {
  enableInstitutionSelection: boolean,
  enablePublisherSelection: boolean,
  enableCompress: boolean,
  enableVerticalBars: boolean
  enableMultYear: boolean,
  enableCorrespondingInstitution: boolean
}

export interface SelectionFilterData {
  showUnknown: boolean,
  selectedInstitution: string,
  selectedPublisher: string,
  years: MinMax,
  compress: boolean,
  stackPercent: boolean,
  multYear: boolean,
  verticalBars: boolean,
  correspondingInstitution: boolean,
}

export interface PublicationFilterOptions {
  color?: string,
  institutionId?: string,
  disableDownloadButton?: boolean,
}

export interface CommonData {
  oaColors: Array<OAColor>
  oaColorsReduced: Array<OAColor>
  institutions: Array<Institution>
  inactiveInstitutions: Array<Institution>
  publicationTypes: Array<PublicationType>
}

export interface PublisherData {
  publishers: Array<Publisher>
}

export interface PublicationStatsPair {
  numbers: Array<PublicationColor>
  numbersPerInstitution: Array<PublicationColor>
}

export interface PublicationStatsPublisher extends PublicationStatsPair {
  publishers: Array<Publisher>
}

export interface PublicationPublisherStats {
  publishers: Array<Publisher>
  numbersPerYearAndInstitution: Map<string, Array<PublicationColor>>
}

export interface PublicationPublicationTypeStats {
  publicationTypes: Array<PublicationType>
  numbersPerYearAndInstitution: Map<string, Array<PublicationColor>>
}

export interface OAColor {
  name: string
  color: string
}

export interface Institution {
  id: string
  name: string
  sname: string
  nameEn: string
  fileDelivery: boolean
}

export interface PublicationColor {
  name: string
  value: number
  year: number
  institutionId: string
  publisherId: string
  mainPublisherId: string
  corresponding: boolean
}

export interface DiagramData {
  totalCount?: number
  totalValues: Array<PublicationColor>
}

export interface ClassificationResult {
  publication: Publication
  coat: Coat
  explanation: Explanation
}

export interface Explanation {
  steps: Array<string>
  crossref: object
  unpaywall: object
  doaj: object
  openapc: object
  romeo: object
  openalex: object
}

export interface SourceReference {
  institutionId : string
  nativeId : string
  corr : boolean
}

export interface Author {
  firstName: string
  lastName: string
  corr: boolean
}

export interface PublicationType {
  id: number
  name: string
}


export interface PublicationHeader {
  id: string
  doi: string
  title: string
  year: number
  pubtypeId: string
  publisher: string
  publisherId: string
  mainPublisher: string
  mainPublisherId: string
  color: string,
}

export interface PublicationSource {
  nativeid: string
  doi: string
  title: string
  pubtype: string
  record: object
}

export interface FetchResult {
  importedRecords: number
  createdRecords: number
  modifiedRecords: number
  unchangedRecords: number
  ignoredRecords: number
  publicationSources: Array<PublicationSource>
}

export interface UploadResponse {
  key: string
  fetchResult: FetchResult
}

export interface Publication {
  id: string
  doi: string
  title: string
  year: number
  type: PublicationType
  date: number
  coat: string
  color: string
  colorUpw: string
  authors: Array<Author>
  publisher: Publisher
  mainPublisher: Publisher
  journal: Journal
  licence: Licence
  embargoTime?: string
  version?: string
  costs: Costs
  oaVersionLink?: string
  oaPlace?: string
//  oaUrl?: string
  sources?: Array<SourceReference>
}

export interface Coat {
  name: string
  place: number
  licence: number
  version: number
  embargo: number
  conditions: number

  srcPlace: string,
  srcLicence: string
  srcVersion: string
  srcEmbargo: string
  srcConditions: string
}

export interface Publisher {
  id: string
  name: string
  wikidataId?: string
  parentName: string
}

export interface Journal {
  id: string
  title: string
  wikidataId: string
  issn: Array<string>
}
export interface Costs {
  amount: string,
  currency: string
}

export interface Licence {
  licence: string,
  url: string
}

export interface ExcelDownloadOptions {
  includePublicationId: boolean
  includeMetaSources: boolean
  includeNativeIds: boolean
  includeAuthor: boolean
  multipleRows: boolean // Output has one row for every publication for every connected institution
}

export interface PublicationFilter {
  year: number
  institution: string
  color: string
  colorUpw: string
  doi: string
  author: string
  publisher: string
  filterMainPublisher: boolean
  includeUnknown: boolean
  options: ExcelDownloadOptions
}

export interface DownloadRequest {
  key?: string
  filter?: PublicationFilter
}

export const coatDescription = {
  keys: ['place', 'licence', 'version', 'embargo', 'conditions'],
  place : ['', 'Source is OA', 'OA version is available in repository', 'Other OA version', 'No OA'],
  licence : ['', 'Open License', 'Free License', 'Proprietary license', 'No license / license unknown'],
  version : ['', 'Publisher\'s version', 'Postprint or last submitted version', 'Preprint or first submitted version', 'Unknown'],
  embargo : ['', 'No embargo', 'Up to 6 months', 'Up to 12 months', 'More than 12 months / unknown'],
  conditions : ['', 'Free', 'Paid in OA media', 'Paid in subscription media', 'No OA']
}

export interface WebSocketMessage {
  cmd: string
  data: object
}

/*
export interface AuthState {
  token: string
  refreshToken: string
  status: string
  user: object
}

 */

export interface User {
  name: string
  admin: boolean
  role: string
}

export interface Faq {
  number: number
  question: string
  answer: string
}
