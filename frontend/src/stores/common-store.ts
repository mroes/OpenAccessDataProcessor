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

import { defineStore, PiniaPluginContext } from 'pinia'
import { CommonData, Institution, OAColor, PublicationType, Publisher, PublisherData } from 'components/models'
import { fetchCommonData, fetchPublisherData } from 'src/defs/api'
import { useSettingsStore } from 'stores/settings-store'
import { OA_UNKNOWN } from 'src/defs/const'

const licences = [
  'CC0',
  'CC BY',
  'CC BY-SA',
  'PD',
  'CC BY-NC',
  'CC BY-NC-SA',
  'CC BY-ND',
  'CC BY-NC-ND',
  'other',
  'none'
]

// colours for licences
const licenceColors = ['#FFC312',
  '#C4E538',
  '#12CBC4',
  '#F79F1F',
  '#A3CB38',
  '#1289A7',
  '#EE5A24',
  '#009432',
  '#0652DD',
  '#EA2027',
  '#006266',
  '#1B1464'
]

const settingsStore = useSettingsStore()

function buildInstitutionMap(institutions: Array<Institution>) {
  const institutionMap = new Map<string,Institution>
  institutions.forEach(inst => institutionMap.set(inst.id, inst))
  return institutionMap
}

function extractId(url: string): string {
  return url.substring(url.lastIndexOf('/') + 1);
}

export const useCommonStore = defineStore('common', {
  state: () => ({
    initialized: false,
    common: {
      oaColors: [] as Array<OAColor>,
      oaColorsReduced: [] as Array<OAColor>,
      institutions: [] as Array<Institution>,
      inactiveInstitutions: [] as Array<Institution>,
      publicationTypes: [] as Array<PublicationType>
    } as CommonData,
    publisher: {
      publishers: [] as Array<Publisher>
    } as PublisherData,
    institutionMap: new Map<string, Institution>
  }),
  getters: {
    getInstitutions: (state) => {
      return state.common.institutions
    },
    getInactiveInstitutions: (state) => {
      return state.common.inactiveInstitutions
    },
    getInstitutionsWithFileDelivery: (state) => {
      return state.common.institutions.filter( inst => inst.fileDelivery)
    },
    isExistingRor: (state) => (rorId : string) => {
      if (state.common.institutions && state.common.institutions.length > 0) {
        return state.common.institutions.find ( inst => {
          if (rorId === extractId(inst.id)) {
            return true
          }
        })
      }
      return false
    },
    labelForInstitution: (state) => (id : string) => {
      if (state.institutionMap && state.institutionMap.size > 0) {
        const institution = state.institutionMap.get(id)
        if (institution) {
          return institution.name
        } else {
          return '?'
        }
      }
      return ''
    },
    getAllOAColors: (state) => {
      if (state.common.oaColors) {
        return state.common.oaColors
      }
      return []
    },
    getOAColors: (state) => {
      if (state.common.oaColors) {
        return state.common.oaColors.filter(oac =>
          (settingsStore.chartSettings.showUnknown || (oac.name !== OA_UNKNOWN))
        )
      }
      return []
    },
    getOAColorsReduced: (state) => {
      if (state.common.oaColorsReduced) {
        return state.common.oaColorsReduced.filter(oac =>
          (settingsStore.chartSettings.showUnknown || (oac.name !== OA_UNKNOWN))
        )
      }
      return []
    },
    getLicences: () => {
      return licences
    },
    getLicenceColors: () => {
      return licenceColors
    },
    getPublicationType: (state) => (id : number) => {
      if (state.common.publicationTypes) {
        return state.common.publicationTypes.find(pub => pub.id === id)
      }
      return undefined
    },
    getPublicationTypes: (state) => {
      return state.common.publicationTypes
    },
    labelForPublicationType: (state) => (id : number) => {
      if (state.common.publicationTypes && state.common.publicationTypes.length > 0) {
        const pubtype = state.common.publicationTypes.find(pt => pt.id == id)
        if (pubtype) {
          return pubtype.name
        } else {
          return 'other'
        }
      }
      return id
    },
    getPublishers: (state) => {
      return state.publisher.publishers
    },
    getPublisher: (state) => (id : string) => {
      if (state.publisher.publishers) {
        return state.publisher.publishers.find(pub => pub.id === id)
      }
      return undefined
    },
    labelForPublisher: (state) => (id : string) => {
      if (state.publisher.publishers && state.publisher.publishers.length > 0) {
        const publisher = state.publisher.publishers.find(pt => pt.id == id)
        if (publisher) {
          return publisher.name
        } else {
          return '?'
        }
      }
      return id
    }

  },
  actions: {
    async fetchCommonData() {
      if (!this.initialized) {
        fetchCommonData().then(stats => {
          this.common.oaColors = stats.oaColors
          this.common.oaColorsReduced = stats.oaColorsReduced
          this.common.institutions = stats.institutions
          this.common.inactiveInstitutions = stats.inactiveInstitutions
          this.institutionMap = buildInstitutionMap(stats.institutions)
          this.common.publicationTypes = stats.publicationTypes
          this.initialized = true
        })
          .catch(error => {
            console.error(error)
          })
      }
    },
    async fetchPublisherData() {
      if (!this.initialized) {
        // console.log('fetch publishers')
        fetchPublisherData().then(data => {
          // console.log('publishers received')
          this.publisher.publishers = data.publishers
        })
          .catch(error => {
            console.error(error)
          })
      }
    }

  },
  persist: {
    storage: sessionStorage,
    debug: true,
    afterHydrate: (context: PiniaPluginContext) => {
      // rebuild the map because it is not persisted by the Plugin
      if (context.store.common.institutions) {
        context.store.institutionMap = buildInstitutionMap(context.store.common.institutions)
      }
    }
  }
})

// Automatically call fetchCommonData when the store is instantiated
const store = useCommonStore()
store.fetchCommonData()
store.fetchPublisherData()
