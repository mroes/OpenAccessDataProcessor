<!--
  -  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
  -
  -  Permission is hereby granted, free of charge, to any person obtaining a copy
  -  of this software and associated documentation files (the "Software"), to deal
  -  in the Software without restriction, including without limitation the rights
  -  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  -  copies of the Software, and to permit persons to whom the Software is
  -  furnished to do so, subject to the following conditions:
  -
  -  The above copyright notice and this permission notice shall be included in all
  -  copies or substantial portions of the Software.
  -
  -  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  -  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  -  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  -  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  -  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  -  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  -  SOFTWARE.
  -->

<template>
  <q-page class="padding">
    <div class="row">
      <div class="col bordered self-start">
        <div class="text-h6">{{ $t('index_header') }}</div>
        <p>{{ $t('index_text1') }}</p>
      </div>
    </div>

    <div class="row">
      <div class="col bordered" style="min-width: 200px; max-width: 300px">
        <q-select v-model="selectedInstitution"
                  outlined :options="institutions" :label="$t('institution')"
                  option-value="id"
                  option-label="name"
                  emit-value
                  map-options
                  @update:model-value="setSelectedInstitution"
        />
        <div class="q-pa-md">
          <div class="" style="color: black; background-color: white; margin-left: -15px; margin-bottom: 5px">
            {{ $t('publication_year') }}
          </div>
          <q-range
            v-model="years"
            :min="yearsMinMax.min"
            :max="yearsMinMax.max"
            :step="1"
            label-always
            @update:model-value="updateYear"
            style="padding-top: 25px"
          />
          <div class="" style="margin-left: -25px">
            <q-toggle
              v-model="showUnknown"
              :label="$t('showUnknown')"
              @update:model-value="showUnknownChanged"
            >
              <q-tooltip class="text-body2" :delay="500">
                {{ $t('tt_showUnknown') }}
              </q-tooltip>
            </q-toggle>
          </div>
        </div>
      </div>

      <div class="col bordered" style="min-width: 650px">
        <PublicationChart :data="getValuesTotal"
                          :year-range="years"
                          :show-unknown="showUnknown"
                          style="height: 200px">
        </PublicationChart>
      </div>

    </div>

    <div class="row">
      <div class="col-grow bordered" style="min-height: 400px; height: 420px">
        <PublicationPerYearChart
          :values="getValues"
          :year-range="years"
          :by-color="true"
          :compress="true"
          info-text-key="index_barchart_text"
          style="height: 400px"
        >
        </PublicationPerYearChart>
      </div>
    </div>

  </q-page>
</template>

<script lang="ts">
import { ChartSettings, MinMax, PublicationColor } from 'components/models'
import { computed, defineComponent, nextTick, ref } from 'vue'
import PublicationChart from 'components/charts/PublicationChart.vue'
import { usePublicationStore } from 'stores/publication-store'
import PublicationPerYearChart from 'components/charts/PublicationPerYearChart.vue'
import { useSettingsStore } from 'stores/settings-store'
import { ALL_INSTITUTIONS } from 'src/defs/const'
import { Validate } from 'components/QuasarModels'
import { currentYear } from 'src/defs/dateutil'
import { sumPerOAColor } from 'src/defs/chartutil'
import { useRoute } from 'vue-router'
import { buildInstitutionOptions } from 'src/defs/options_support'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'PageIndex',
  components: {
    PublicationChart,
    PublicationPerYearChart
  },
  setup() {

    const { t, locale } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const yearsMinMax = {
      min: 2014,
      max: currentYear()
    }

    const startYearRef = ref<Validate>()
    const endYearRef = ref<Validate>()

    const publicationStore = usePublicationStore()
    publicationStore.fetchPublicationStats()

    const settingsStore = useSettingsStore()
    const form = ref<ChartSettings>(Object.assign({}, settingsStore.chartSettings))

    const years = ref<MinMax>({
      min: yearsMinMax.min,
      max: yearsMinMax.max
    })

    const showUnknown = ref<boolean>(
      settingsStore.getShowUnknown
    )

    const route = useRoute()
    if (route.query) {
      if (route.query.institutionid) {
        if (Array.isArray(route.query.institutionid)) {
        } else {
          const institutionId = route.query.institutionid
          if (institutionId) {
            settingsStore.setSelectedInstitution(institutionId)
          }
        }
      }
      if (route.query.lang) {
//        console.log('Language: ' + route.query.lang)
        locale.value = route.query.lang as string
      }
    }
    settingsStore.setLocale(locale.value)

    const selectedInstitution = ref<string>(settingsStore.getSelectedInstitutionId)

    const setSelectedInstitution = () => {
      settingsStore.setSelectedInstitution(selectedInstitution.value)
    }

    const updateYear = async () => {
      await nextTick()
    }

    // values for the bar chart
    const getValues = computed(() => {
      let values: Array<PublicationColor>
      if (selectedInstitution.value === ALL_INSTITUTIONS) {
        values = publicationStore.getNumbersPerYear
      } else {
        const numbersPerInstitution = publicationStore.getNumbersPerYearAndInstitutionAndPublisher
        if (numbersPerInstitution) {
          values = numbersPerInstitution.filter(pc => pc.institutionId === selectedInstitution.value)
        } else {
          values = []
        }
      }
      if (values) {
        values = values.filter(pc => pc.year >= years.value.min && pc.year <= years.value.max)
      }
      return values
    })

    // // values for the pie chart
    const getValuesTotal = computed(() => {
      let values = getValues.value
      if (values) {
        values = sumPerOAColor(values)
      }
      return {
        totalValues: values
      }
    })

    const showUnknownChanged = (showUnknown: boolean) => {
      settingsStore.setShowUnknown(showUnknown)
    }

    return {
      startYearRef,
      endYearRef,
      institutions: buildInstitutionOptions(t),
      form,
      yearsMinMax,
      years,
      showUnknown,
      selectedInstitution,
      setSelectedInstitution,
      updateYear,
      showUnknownChanged,
      getValuesTotal,
      getValues
    }
  }
})
</script>

<style scoped>
.title {
  font-size: large;
}
</style>
