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
import { ChartSettings, ExcelDownloadOptions } from 'components/models'
import { ALL_INSTITUTIONS } from 'src/defs/const'
import { currentYear } from 'src/defs/dateutil'

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    locale: '',
    selectedInstitutionId: ALL_INSTITUTIONS,
    chartSettings: {
      showUnknown: false,
      startYear: 2014,
      endYear: currentYear()
    } as ChartSettings,
    downloadOptions: {
      includePublicationId: true,
      includeNativeIds: false,
      includeMetaSources: false,
      includeAuthor: false,
      multipleRows: true,
    } as ExcelDownloadOptions
  }),
  getters: {
    getSelectedInstitutionId: (state) => state.selectedInstitutionId,
    getShowUnknown: (state) => state.chartSettings.showUnknown,
    getLocale: (state): string => state.locale,
    getExcelDownloadOptions: (state) => state.downloadOptions,
  },
  actions: {
    setLocale(locale: string) {
      this.locale = locale
    },
    setShowUnknown(showUnknown: boolean) {
      this.chartSettings.showUnknown = showUnknown
    },
    setExcelDownloadOptions(downloadOptions: ExcelDownloadOptions) {
      this.$patch({ downloadOptions })
    },
    setSelectedInstitution(institutionId : string) {
      this.selectedInstitutionId = institutionId
    }
  },
  persist: [
    {
      pick: ['downloadOptions'],
      storage: localStorage
    },
    {
      pick: ['chartSettings', 'selectedInstitutionId', 'locale'],
      storage: sessionStorage
    }
  ]
})
