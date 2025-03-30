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

import axios, { AxiosResponse } from 'axios'
import {
  ClassificationResult,
  CommonData,
  Faq,
  Publication,
  PublicationFilter,
  PublicationHeader,
  PublicationStatsPair,
  PublicationStatsPublisher,
  Publisher, PublisherData
} from 'components/models'


export function fetchCommonData () {
  return new Promise<CommonData>((resolve, reject) => {
      axios.get('api/publication/commondata').then((response : AxiosResponse<CommonData>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function fetchPublisherData () {
  return new Promise<PublisherData>((resolve, reject) => {
      axios.get('api/publication/publisherdata').then((response : AxiosResponse<PublisherData>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function fetchPublicationStats () {
  return new Promise<PublicationStatsPublisher>((resolve, reject) => {
    axios.get('api/publication/stats').then((response : AxiosResponse<PublicationStatsPublisher>) => {
      resolve(response.data)
    })
      .catch(error => {
        reject(error)
      })
  }
  )
}


export function fetchPublicationStatsByPublicationType () {
  return new Promise<PublicationStatsPair>((resolve, reject) => {
      axios.get('api/publication/stats/publicationtype').then((response : AxiosResponse<PublicationStatsPair>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function fetchPublicationStatsByLicence () {
  return new Promise<PublicationStatsPair>((resolve, reject) => {
      axios.get('api/publication/stats/licence').then((response : AxiosResponse<PublicationStatsPair>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function classifyPublication (doi: string) {
  return new Promise<ClassificationResult>((resolve, reject) => {
    const encodedDoi = btoa(doi)
      axios.get('api/publication/classify/' + encodedDoi).then((response : AxiosResponse<ClassificationResult>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function fetchPublication (id : string) {
  return new Promise<Publication>((resolve, reject) => {
      axios.get('api/publication/' + id)
        .then((response : AxiosResponse<Publication>) => {
          resolve(response.data)
        })
        .catch(error => {
//          console.log('error in fetchPublication')
          console.log(error)
          reject(error)
        })
    }
  )
}

export function fetchPublications (options : PublicationFilter) {
  return new Promise<Array<PublicationHeader>>((resolve, reject) => {
    axios.post('api/publication/list', options)
        .then((response : AxiosResponse<Array<PublicationHeader>>) => {
        resolve(response.data)
      })
        .catch(error => {
          console.log(error)
          reject(error)
        })
    }
  )
}

export function fetchAllPublishers (date : string) {
  return new Promise<Array<Publisher>>((resolve, reject) => {
      axios.get('api/publisher/list/' + date).then((response : AxiosResponse<Array<Publisher>>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function apiExecuteCommand (cmd : string) {
  return new Promise<AxiosResponse>((resolve, reject) => {
      axios.get('api/admin/cmd/' + cmd).then((response : AxiosResponse) => {
        resolve(response)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function getProcessingState () {
  return new Promise<string>((resolve, reject) => {
      axios.get('api/admin/processing/state').then((response : AxiosResponse<string>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function fetchFaqs (language: string) {
  return new Promise<Array<Faq>>((resolve, reject) => {
      axios.get('api/faq/list?lang=' + language).then((response : AxiosResponse<Array<Faq>>) => {
        resolve(response.data)
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}

export function importPublications (parameters: object) {
  return new Promise<string>((resolve, reject) => {
      axios.post('api/publication/import', parameters).then(() => {
        resolve('')
      })
        .catch(error => {
          reject(error)
        })
    }
  )
}
