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

import { defineStore } from 'pinia'
import {
  fetchPublicationStats,
  fetchPublicationStatsByLicence,
  fetchPublicationStatsByPublicationType
} from 'src/defs/api'
import { PublicationColor, PublicationStatsPair, PublicationStatsPublisher } from 'components/models'
import { useSettingsStore } from 'stores/settings-store'
import { OA_UNKNOWN } from 'src/defs/const'
import { Notify, QSpinnerGears } from 'quasar'
import { Mutex } from 'src/defs/common/mutex'
import { translate } from 'boot/i18n'


const mutex = new Mutex()
const settingsStore = useSettingsStore()

const unknownFilter = (pc : PublicationColor) =>
  pc && (settingsStore.chartSettings.showUnknown || (pc.name !== OA_UNKNOWN))

function setMainPublisher(values: Array<PublicationColor>) {
  values.forEach(pc => {
    // if there is a main publisher, use it
    if (pc.mainPublisherId) {
      pc.publisherId = pc.mainPublisherId
    }
  })
}

export const usePublicationStore = defineStore('publication', {
  state: () => ({
    stats: {} as PublicationStatsPublisher,
    statsPerType: {} as PublicationStatsPair,
    statsPerLicence: {} as PublicationStatsPair
  }),
  getters: {
    getNumbersPerYear: (state) => {
      if (state.stats.numbers) {
        return state.stats.numbers.filter(unknownFilter)
      }
      return []
    },
    getNumbersPerYearAndInstitutionAndPublisher: (state) => {
      if (state.stats.numbersPerInstitution) {
        return state.stats.numbersPerInstitution.filter(unknownFilter)
      }
      return []
    },
    getNumbersPerYearAndInstitutionAndPublicationType: (state) => {
      if (state.statsPerType && state.statsPerType.numbersPerInstitution) {
        return state.statsPerType.numbersPerInstitution.filter(unknownFilter)
      }
      return []
    },
    getNumbersPerYearAndPublicationType: (state) => {
      if (state.statsPerType && state.statsPerType.numbers) {
        return state.statsPerType.numbers.filter(unknownFilter)
      }
      return []
    },
    getNumbersPerYearAndInstitutionAndLicence: (state) => {
      if (state.statsPerLicence && state.statsPerLicence.numbersPerInstitution) {
        return state.statsPerLicence.numbersPerInstitution.filter(unknownFilter)
      }
      return []
    },
    getNumbersPerYearAndLicence: (state) => {
      if (state.statsPerLicence && state.statsPerLicence.numbers) {
        return state.statsPerLicence.numbers.filter(unknownFilter)
      }
      return []
    }
  },
  actions: {
    async fetchPublicationStats() {
      if (!this.stats.numbersPerInstitution || this.stats.numbersPerInstitution.length == 0) {
        const lockAcquired = await mutex.acquire();

        if (!lockAcquired) {
//          console.log('Mutex is locked, returning immediately.')
          return Promise.resolve(this.stats); // Return immediately if lock is already held
        }
//        console.log('Mutex is free, running.')
          const dismissNotify = Notify.create({
            spinner: QSpinnerGears,
            message: translate('msg_fetching_data'),
            timeout: 0,
            color: 'green-5',
            textColor: 'black'
          })
          return new Promise<PublicationStatsPublisher>((resolve, reject) => {
            fetchPublicationStats().then(stats => {
              setMainPublisher(stats.numbersPerInstitution)
              setMainPublisher(stats.numbers)
              this.stats.numbersPerInstitution = stats.numbersPerInstitution
              this.stats.numbers = stats.numbers
              resolve(stats)
            })
              .catch(error => {
                console.log(error)
                return reject(error)
              })
              .finally( () => {
                dismissNotify()
//                console.log('release Mutex')
                mutex.release()
              })
          })
      } else {
        return Promise.resolve(this.stats);  // Return a resolved promise if the stats are already available
      }
    },
    async fetchPublicationStatsByPublicationType() {
      if (!this.statsPerType.numbersPerInstitution || this.statsPerType.numbersPerInstitution.length == 0) {
        fetchPublicationStatsByPublicationType().then(stats => {
          this.statsPerType.numbersPerInstitution = stats.numbersPerInstitution
          this.statsPerType.numbers = stats.numbers
        })
          .catch(error => {
            console.log(error)
          })
      }
    },
    async fetchPublicationStatsByLicence() {
      if (!this.statsPerLicence.numbersPerInstitution || this.statsPerLicence.numbersPerInstitution.length == 0) {
        fetchPublicationStatsByLicence().then(stats => {
          this.statsPerLicence.numbersPerInstitution = stats.numbersPerInstitution
          this.statsPerLicence.numbers = stats.numbers
        })
          .catch(error => {
            console.log(error)
          })
      }
    }

  },
  persist: false
})
