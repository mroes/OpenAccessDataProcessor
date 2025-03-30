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

import { computed } from 'vue'
import { ALL_INSTITUTIONS } from 'src/defs/const'
import { ComposerTranslation } from 'vue-i18n'
import { useCommonStore } from 'stores/common-store'
import { Institution } from 'components/models'
import { useSettingsStore } from 'stores/settings-store'

const commonStore = useCommonStore()
const settingsStore = useSettingsStore()

export function buildInstitutionOptions(t: ComposerTranslation, withAll = true) {
  return computed( () => {
    const institutions : Institution[] = []
    const lang = settingsStore.getLocale;

    (commonStore.getInstitutions || []).forEach ( (institution: Institution) => {
        institutions.push( {
          id: institution.id,
          name: lang.startsWith('de') ? institution.name : institution.nameEn
        } as Institution)
    })

    institutions.sort((a, b) => a.name.localeCompare(b.name));
    if (withAll) {
      const allInstitutions = {
        id: ALL_INSTITUTIONS,
        name: t('allinstitutions')
      } as Institution
      const items = [allInstitutions].concat(institutions)
      return items
    } else {
      return institutions
    }
  })
}

export function buildPublicationTypeOptions() {
  return computed( () => {
    const publicationTypes = commonStore.getPublicationTypes
    return publicationTypes
  })
}
