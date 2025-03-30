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
  <q-page class="padding" style="margin-left: 5px;margin-right: 5px">
    <div class="row">
      <div class="col" style="max-width: 800px">
        <SelectionFilter
          ref="selectionFilter"
          :filter-data="filter"
          :filter-options="filterOptions"
          :publishers="sortedPublishers"
          @filter-changed="filterChanged"
        ></SelectionFilter>
      </div>
    </div>

    <q-tabs
      v-model="tab"
      dense
      class="text-grey"
      active-color="primary"
      indicator-color="primary"
      align="justify"
      narrow-indicator
      @update:model-value="tabChanged"
    >
      <q-tab name="pubs1" :label="$t('label_oa_per_year')" :no-caps="true" />
      <q-tab name="pubs2" :label="$t('label_oa_per_publisher')" :no-caps="true" />
      <q-tab name="pubs3" :label="$t('label_inst_per_year')" :no-caps="true" />
      <q-tab name="pubs4" :label="$t('label_oa_per_publicationtype')" :no-caps="true" />
      <q-tab name="pubs5" :label="$t('label_licence_per_oa')" :no-caps="true" />
      <q-tab name="pubs6" :label="$t('label_licence_per_year')" :no-caps="true" />
    </q-tabs>

    <q-separator />

    <q-tab-panels v-model="tab" animated>
      <q-tab-panel name="pubs1">
        <span class="title">{{ $t('title_oa_per_year') }}</span>
        <div class="col-grow bordered box" :style="tabStyle">
          <PublicationPerYearChart :values="getValuesByYear"
                                   :year-range="filter.years"
                                   :compress="filter.compress"
                                   :stack-for-closed="stackForClosed"
                                   :stack-for-unknown="stackForUnknown"
                                   :stack-percent="filter.stackPercent"
                                   style="height: 400px"></PublicationPerYearChart>
        </div>
      </q-tab-panel>

      <q-tab-panel name="pubs2">
        <span class="title">{{ $t('title_oa_per_publisher') }}</span>
        <div class="col-grow bordered box" style="min-height: 400px; height: 320px">
          <PublicationPerPublisherChart :values="getPublisherValues"
                                        :compress="filter.compress"
                                        :stack-for-closed="stackForClosed"
                                        :stack-for-unknown="stackForUnknown"
                                        :stack-percent="filter.stackPercent"
                                        :vertical-bars="filter.verticalBars"
                                        style="height: 400px"></PublicationPerPublisherChart>
        </div>
      </q-tab-panel>

      <q-tab-panel name="pubs3">
        <span class="title">{{ $t('title_inst_per_year') }}</span>
        <div class="col-grow bordered box" style="min-height: 400px; height: 320px">
          <PublicationPerYearChart :values="getValuesByInstitution"
                                   :year-range="filter.years"
                                   :by-color="false"
                                   :compress="false"
                                   :stack-for-closed="stackForClosed"
                                   :stack-for-unknown="stackForUnknown"
                                   :stack-percent="filter.stackPercent"
                                   style="height: 400px"></PublicationPerYearChart>
        </div>
      </q-tab-panel>

      <q-tab-panel name="pubs4">
        <span class="title">{{ $t('title_oa_per_publicationtype') }}</span>
        <div class="col-grow bordered box" style="min-height: 400px; height: 320px">
          <PublicationPerPublicationTypeChart :values="getValuesByPublicationType"
                                              :compress="filter.compress"
                                              :stack-for-closed="stackForClosed"
                                              :stack-for-unknown="stackForUnknown"
                                              :stack-percent="filter.stackPercent"
                                              style="height: 400px"></PublicationPerPublicationTypeChart>
        </div>
      </q-tab-panel>

      <q-tab-panel name="pubs5">
        <span class="title">{{ $t('title_licence_per_oa') }}</span>
        <div class="col-grow bordered box" style="min-height: 400px; height: 320px">
          <PublicationPerLicenceChart :values="getValuesByLicence"
                                      :year-range="filter.years"
                                      :compress="filter.compress"
                                      :stack-for-closed="stackForClosed"
                                      :stack-for-unknown="stackForUnknown"
                                      :stack-percent="filter.stackPercent"
                                      :vertical-bars="filter.verticalBars"
                                      style="height: 400px"></PublicationPerLicenceChart>
        </div>
      </q-tab-panel>

      <q-tab-panel name="pubs6">
        <span class="title">{{ $t('title_licence_per_year') }}</span>
        <div class="col-grow bordered box" style="min-height: 400px; height: 320px">
          <PublicationPerLicenceYearChart :values="getValuesByLicence"
                                          :year-range="filter.years"
                                          :compress="filter.compress"
                                          :stack-for-closed="stackForClosed"
                                          :stack-for-unknown="stackForUnknown"
                                          :stack-percent="filter.stackPercent"
                                          :vertical-bars="filter.verticalBars"
                                          :line-chart="true"
                                          style="height: 400px"></PublicationPerLicenceYearChart>
        </div>
      </q-tab-panel>

    </q-tab-panels>

  </q-page>
</template>

<script lang="ts">
import { ChartSettings, Publisher, SelectionFilterData, SelectionFilterOptions } from 'components/models'
import {computed, defineComponent, ref} from 'vue';
import {usePublicationStore} from 'stores/publication-store'
import PublicationPerYearChart from 'components/charts/PublicationPerYearChart.vue'
import {useSettingsStore} from 'stores/settings-store'
import { ALL_INSTITUTIONS, ALL_PUBLISHERS, UNKNOWN_PUBLISHER } from 'src/defs/const'
import PublicationPerPublisherChart from 'components/charts/PublicationPerPublisherChart.vue'
import {
  sumPerPublisher,
  sumPerPublisherAndColorAndYear,
  sumPerPublisherAndInstitutionAndYear
} from 'src/defs/chartutil'
import SelectionFilter from 'components/charts/SelectionFilter.vue'
import PublicationPerPublicationTypeChart from 'components/charts/PublicationPerPublicationTypeChart.vue'
import PublicationPerLicenceYearChart from 'components/charts/PublicationPerLicenceYearChart.vue'
import PublicationPerLicenceChart from 'components/charts/PublicationPerLicenceChart.vue'
import { useCommonStore } from 'stores/common-store'

const MAX_PUBLISHERS = 10

export default defineComponent({
  name: 'PageIndex',
  components: {
    SelectionFilter,
    PublicationPerYearChart,
    PublicationPerPublisherChart,
    PublicationPerPublicationTypeChart,
    PublicationPerLicenceChart,
    PublicationPerLicenceYearChart
  },
  setup() {

    const commonStore = useCommonStore()

    const publicationStore = usePublicationStore()
    publicationStore.fetchPublicationStats()
    publicationStore.fetchPublicationStatsByPublicationType()
    publicationStore.fetchPublicationStatsByLicence()

    const settingsStore = useSettingsStore()
    const chartSettings = ref<ChartSettings>(Object.assign({}, settingsStore.chartSettings))

    const selectionFilter = ref<InstanceType<typeof SelectionFilter> | null>(null)
    const tab = ref('pubs1')

    const filterOptions = ref<SelectionFilterOptions>({
      enableInstitutionSelection: true,
      enablePublisherSelection: true,
      enableCompress: true,
      enableVerticalBars: false,
      enableMultYear: true,
      enableCorrespondingInstitution: true
    })

    const filter = ref<SelectionFilterData>({
      selectedInstitution : settingsStore.getSelectedInstitutionId,
      selectedPublisher: ALL_PUBLISHERS,
      years: {
        min: chartSettings.value.startYear,
        max: chartSettings.value.endYear
      },
      showUnknown: settingsStore.getShowUnknown,
      compress : true,
      stackPercent : false,
      multYear: true,
      verticalBars: true,
      correspondingInstitution: false
    } as  SelectionFilterData)

    let previousTab = 'pub1'
    let savedMultYear = true
    const savedYears = {
      min: filter.value.years.min,
      max: filter.value.years.max
    }
    const tabChanged = (newTab: string) => {
      filterOptions.value.enableInstitutionSelection = true
      filterOptions.value.enablePublisherSelection = true
      filterOptions.value.enableCompress = true
      filterOptions.value.enableVerticalBars = false
      filterOptions.value.enableMultYear = true
      filterOptions.value.enableCorrespondingInstitution = true;
      if (newTab == 'pubs5') {
        savedMultYear = filter.value.multYear
        savedYears.min = filter.value.years.min
        savedYears.max = filter.value.years.max
        if (filter.value.multYear) {
          filter.value.years.min = filter.value.years.max
        }

      } else if (previousTab == 'pubs5') {
        filter.value.multYear = savedMultYear
        filter.value.years.min = savedYears.min
        filter.value.years.max = savedYears.max
      }

      switch (newTab) {
        case 'pubs1' :
          break
        case 'pubs2' :
          filterOptions.value.enablePublisherSelection = false
          filterOptions.value.enableVerticalBars = true
          break
        case 'pubs3' :
          filterOptions.value.enableInstitutionSelection = false
          filterOptions.value.enableCompress = false
          break
        case 'pubs4' :
          filterOptions.value.enablePublisherSelection = false
          break
        case 'pubs5' :
          filterOptions.value.enablePublisherSelection = false
          filterOptions.value.enableVerticalBars = true
          filterOptions.value.enableMultYear = false
          filter.value.multYear = false
          break
        case 'pubs6' :
          filterOptions.value.enableCompress = false
          filterOptions.value.enablePublisherSelection = false
          break
      }

      selectionFilter.value?.setValue(filter.value)
      previousTab = newTab
    }

    const filterChanged = ( params : { filter : SelectionFilterData}) => {
      filter.value = params.filter
      settingsStore.setShowUnknown(filter.value.showUnknown)
    }

    const getValues = computed( () => {
      let values
      if (filter.value.selectedInstitution == ALL_INSTITUTIONS) {
        values = publicationStore.getNumbersPerYear
      } else {
        values = publicationStore.getNumbersPerYearAndInstitutionAndPublisher
        const filterCorresponding = filter.value.correspondingInstitution
        values = values.filter(pc => pc.institutionId === filter.value.selectedInstitution
            && (!filterCorresponding || pc.corresponding) )
      }
      values = values.filter(pc => pc.year >= filter.value.years.min && pc.year <= filter.value.years.max)
      return values
    })

    const getValuesByYear = computed( () => {
      let values = getValues.value
      if (filter.value.selectedPublisher != ALL_PUBLISHERS) {
        values = values.filter(pc => pc.publisherId === filter.value.selectedPublisher)
      }

      return values
    })

    const getPublisherValues = computed( () => {
      let values = getValues.value

      // filter publications with a publisher
      values = values.filter(pc => pc.publisherId)

      return values
    })

    const getValuesByInstitution = computed( () => {
      // values for the bar chart by institution
      let values = publicationStore.getNumbersPerYearAndInstitutionAndPublisher
      const filterCorresponding = filter.value.correspondingInstitution
      values = values.filter(pc => (!filterCorresponding || pc.corresponding) )

      if (filter.value.selectedPublisher != ALL_PUBLISHERS) {
        values = values.filter(pc => pc.publisherId === filter.value.selectedPublisher)
      }
      values = values.filter(pc => pc.year >= filter.value.years.min && pc.year <= filter.value.years.max)
      values = sumPerPublisherAndInstitutionAndYear(values)

      return values
    })

    const getValuesByPublicationType = computed( () => {
      // values for the bar chart by publication type
      let values
      if (filter.value.selectedInstitution == ALL_INSTITUTIONS) {
        values = publicationStore.getNumbersPerYearAndPublicationType || []
      } else {
        values = publicationStore.getNumbersPerYearAndInstitutionAndPublicationType || []
        const filterCorresponding = filter.value.correspondingInstitution
        values = values.filter(pc => pc.institutionId === filter.value.selectedInstitution
          && (!filterCorresponding || pc.corresponding) )
      }

      values = values.filter(pc => pc.year >= filter.value.years.min && pc.year <= filter.value.years.max)
      return values
    })

    const getValuesByLicence = computed( () => {
      // values for the bar chart by licence
      let values
      if (filter.value.selectedInstitution == ALL_INSTITUTIONS) {
        values = publicationStore.getNumbersPerYearAndLicence || []
      } else {
        values = publicationStore.getNumbersPerYearAndInstitutionAndLicence || []
        const filterCorresponding = filter.value.correspondingInstitution
        values = values.filter(pc => pc.institutionId === filter.value.selectedInstitution
          && (!filterCorresponding || pc.corresponding) )
      }

      values = values.filter(pc => pc.year >= filter.value.years.min && pc.year <= filter.value.years.max)
      values = sumPerPublisherAndColorAndYear(values)
      return values
    })

    const sortedPublishers = computed( () => {
      // build a list of publishers
      // publishers with the most publications shall be displayed on top of the list
      const publicationValues = getPublisherValues.value
      const publishers: Array<Publisher> = commonStore.getPublishers
      if (publicationValues && publishers) {
        // sort descending by amount of publications for Publisher
        const sortedPublisherRecs = sumPerPublisher(publicationValues)
          .sort((pc1, pc2) => {
            return pc2.value - pc1.value
          })
        const topPublishers : Array<Publisher> = []
        const otherPublishers : Array<Publisher> = []
        sortedPublisherRecs.forEach(value => {
          const publisherId = value.publisherId
          if (publisherId == UNKNOWN_PUBLISHER) {
            return
          }
          const publisher = publishers.find(pub => pub.id == publisherId)
          if (publisher) {
            if (topPublishers.length < MAX_PUBLISHERS) {
              topPublishers.push(publisher)
            } else {
              // other publishers
              // only consider publishers with more than n publications
              if (value.value >= 20) {
                otherPublishers.push(publisher)
              }
            }
          } else {
            console.log('No publisher found for publisherId = ' + publisherId)
          }
        })
        otherPublishers.sort((pub1, pub2) => {
          return pub1.name.toLowerCase().localeCompare(pub2.name.toLowerCase())
        })
        // value for a separation line
        const separationItem = {
          id: '-1',
          name: ''
        } as Publisher
        const result = topPublishers.concat(separationItem).concat(otherPublishers)
        return result
      }
      return []
    })

    const tabStyle =  computed( () => {
      return {
        'min-height': '400px',
        'height': 320 + 'px'
      }
    })


    return {
      tab,
      tabStyle,
      filter,
      filterOptions,
      tabChanged,
      selectionFilter,
      getValuesByYear,
      getPublisherValues,
      getValuesByInstitution,
      getValuesByPublicationType,
      getValuesByLicence,
      sortedPublishers,
      filterChanged,
      stackForClosed: true,
      stackForUnknown: true
    }
  }
})
</script>

<style scoped>
.title {
  font-size: larger;
  margin-bottom: 10px;
}
.box {
  min-width: 1200px;
}
</style>
