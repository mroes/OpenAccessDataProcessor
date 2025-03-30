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
  <div v-if="show" class="row bordered" ref="filterBox">
  <div class="col" style="min-width: 300px">
    <q-select v-model="data.selectedInstitution"
              :class="{ invisible: !options.enableInstitutionSelection }"
              outlined
              :options="institutions"
              :label="$t('institution')"
              option-value="id"
              option-label="name"
              emit-value
              map-options
              @update:model-value="filterChanged"
    />
    <div class="" style="margin-left: 10px">
      <div class="" style="color: grey; background-color: white">
        {{ $t('publication_year') }}
      </div>
      <q-range v-if="data.multYear"
        v-model="data.years"
        :min="yearsMinMax.min"
        :max="yearsMinMax.max"
        :step="1"
        @update:model-value="filterChanged"
        style="padding-top: 1px; width: 200px"
      />
      <q-slider v-else
        v-model="data.years.min"
        :min="yearsMinMax.min"
        :max="yearsMinMax.max"
        :step="1"
        style="padding-top: 1px; width: 200px"
        @update:model-value="updateMaxYear"
      />
    </div>
      <div class="row">
      <q-input :model-value="data.years.min" outlined :label="$t('startyear')" type="number" style="width:110px;margin-right: 5px"
               ref="startYearRef"
               :debounce="100"
               :rules="[val => !!val || $t('err_field_required'),
                 val => val >= yearsMinMax.min || $t('err_min_date', [yearsMinMax.min-1]),
                 val => (!data.multYear || val <= data.years.max) || $t('err_min_after_max_date')
                 ]"
               @update:model-value="updateMinYear"
      />
      <q-input v-model="data.years.max" outlined :label="$t('endyear')" type="number" style="width:140px"
               :disable="!data.multYear"
               ref="endYearRef"
               :debounce="100"
               :rules="[val => !!val || $t('err_field_required'),
                 val => val <= yearsMinMax.max || $t('err_max_date', [yearsMinMax.max+1]),
                 val => (!data.multYear || val >= data.years.min) || $t('err_max_before_min_date')
                 ]"
               @update:model-value="filterChanged"
      >
        <template v-slot:after >
          <q-checkbox v-model="data.multYear" size="xs" style="margin-left: -12px"
                      :disable="!options.enableMultYear"
                      @update:model-value="multYearChanged" >
            <q-tooltip class="text-body2" >
              {{ $t('tt_yearRange')}}
            </q-tooltip>
          </q-checkbox>
        </template>
      </q-input>
    </div>

  </div>
  <div class="col-4" style="min-width: 300px">
    <SelectPublisher
      v-model="data.selectedPublisher"
      :class="{ invisible: !options.enablePublisherSelection }"
      :publishers="publishers"
      @update:model-value="filterChanged"
    >
    </SelectPublisher>

    <div class="row q-pt" >
      <q-toggle
        v-model="data.showUnknown"
        :label="$t('showUnknown')"
        @update:model-value="filterChanged"
      >
      <q-tooltip class="text-body2" :delay="500">
        <div class="">{{ $t('tt_showUnknown')}}</div>
      </q-tooltip>
      </q-toggle>
    </div>
    <div class="row q-pt" >
    <q-toggle
      v-model="data.compress"
      :class="{ invisible: !options.enableCompress }"
      :label="$t('reducedColors')"
      @update:model-value="filterChanged"
    >
      <q-tooltip class="text-body2" :delay="500">
        <div class="">{{ $t('tt_reducedColors')}}</div>
      </q-tooltip>
    </q-toggle>
    </div>
    <div class="row q-pt" >
    <q-toggle
      v-model="data.stackPercent"
      :label="$t('stackPercent')"
      @update:model-value="filterChanged"
    >
      <q-tooltip class="text-body2" :delay="500">
        <div class="">{{ $t('tt_stackPercent')}}</div>
      </q-tooltip>
    </q-toggle>
    </div>

    <div class="row q-pt" >
      <q-toggle
        v-model="data.verticalBars"
        :class="{ invisible: !options.enableVerticalBars }"
        :label="$t('verticalBars')"
        @update:model-value="filterChanged"
      >
        <q-tooltip class="text-body2" :delay="500">
          <div class="">{{ $t('tt_verticalBars')}}</div>
        </q-tooltip>
      </q-toggle>
    </div>

    <div class="row q-pt" >
      <q-toggle
        v-model="data.correspondingInstitution"
        :class="{ invisible: !options.enableCorrespondingInstitution }"
        :disable="disableSwitchCorrespondingInstitution()"
        :label="$t('correspondingInstitution')"
        @update:model-value="filterChanged"
      >
        <q-tooltip class="text-body2" :delay="500">
          <div class="">{{ $t('tt_correspondingInstitution')}}</div>
        </q-tooltip>
      </q-toggle>
    </div>

  </div>

    <div class="col">
      <q-space />
      <q-btn icon="fa-regular fa-square-caret-up" size="sm"
             style="width: 10px; margin-left: 5px"
             @click="showFilter(false)"
      />
      <InfoButton text-id="insights_text1" :markdown="true" />
    </div>

    </div>
  <div v-else class="row bordered" >
  <div class="col" style="min-width: 450px"
       @click="showFilter(true)"
       @mouseover="showFilter(true)"
  >
    {{ showFilterSummary() }}
  </div>
  <div class="col">
    <q-space />
    <q-btn icon="fa-regular fa-square-caret-down" size="sm"
           style="width: 10px; margin-left: 5px"
           @click="showFilter(true)"
    />
    <InfoButton text-id="insights_text1" :markdown="true" />
  </div>
</div>
</template>

<script lang="ts">
import { defineComponent, PropType, ref } from 'vue'
import { Publisher, SelectionFilterData, SelectionFilterOptions } from 'components/models'
import { ALL_INSTITUTIONS, ALL_PUBLISHERS } from 'src/defs/const'
import { useI18n } from 'vue-i18n'
import { currentYear } from 'src/defs/dateutil'
import { useCommonStore } from 'stores/common-store'
import { buildInstitutionOptions } from 'src/defs/options_support'
import SelectPublisher from 'components/SelectPublisher.vue'
import InfoButton from 'components/InfoButton.vue'
import { cloneDeep } from 'lodash'
import { debounce } from 'quasar'

export default defineComponent({
  name: 'SelectionFilter',
  components: {
    InfoButton,
    SelectPublisher
  },
  props: {
    filterData: {
      type: Object as PropType<SelectionFilterData>
    },
    filterOptions: {
      type: Object as PropType<SelectionFilterOptions>
    },
    publishers: {
      type: Array<Publisher>,
      required: true
    }

  },
  emits: ['filterChanged'],
  setup(props, context) {

    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning
    const commonStore = useCommonStore()

//    const filterBox = ref<HTMLElement | null>(null)
    const show = ref<boolean>(true)
    const hideOnLeave = ref<boolean>(true)

    const yearsMinMax = {
      min: 2001,
      max: currentYear()
    }


    // // clone data from props, so that data is independent
    const data = ref<SelectionFilterData>(cloneDeep(props.filterData || {} as SelectionFilterData));

    const setValue = (newData : SelectionFilterData) => {
      data.value = Object.assign({}, newData)
    }

    const filterChanged = debounce(() => {
      // await nextTick()
      context.emit('filterChanged', {
          filter: cloneDeep(data.value)
        }
      )
    }, 300)

    const showFilter = (state: boolean) => {
        show.value = state
    }

    const showFilterSummary = () => {
      let result = data.value.years.min.toString()
      if (data.value.multYear) {
        result += ' - ' + data.value.years.max
      }
      if (data.value.selectedInstitution && props.filterOptions?.enableInstitutionSelection) {
        result += ' | '
        result += data.value.selectedInstitution == ALL_INSTITUTIONS ? t('allinstitutions') : commonStore.labelForInstitution(data.value.selectedInstitution)
      }
      if (data.value.selectedPublisher && props.filterOptions?.enablePublisherSelection) {
        if (result) {
          result += ' | '
        }
        result += data.value.selectedPublisher == ALL_PUBLISHERS ? t('allpublishers') : commonStore.labelForPublisher(data.value.selectedPublisher)
      }
      return result
    }

    const updateMinYear = (value: unknown) => {
      // if necessary convert the input value to a number
      // fix for QSlider, interval validator warning
      if (typeof value === 'string') {
        data.value.years.min = parseInt(value)
      } else {
        data.value.years.min = value as number
      }
      updateMaxYear(data.value.years.min)
    }

    const updateMaxYear = (value: number) => {
      if (!data.value.multYear && value) {
        data.value.years.max = value
      }
      filterChanged()
    }

    const multYearChanged = () => {
      if (data.value.multYear) {
        data.value.years.max = currentYear()
      } else {
        data.value.years.min = data.value.years.max
      }
      filterChanged()
    }

    const disableSwitchCorrespondingInstitution = () => {
      return data.value.selectedInstitution === ALL_INSTITUTIONS && props.filterOptions?.enableInstitutionSelection
    }

    return {
      show,
      hideOnLeave,
      yearsMinMax,
      data,
      options: props.filterOptions,
      institutions: buildInstitutionOptions(t),
      setValue,
      filterChanged,
      updateMinYear,
      updateMaxYear,
      multYearChanged,
      showFilter,
      showFilterSummary,
      disableSwitchCorrespondingInstitution
    }
  }
})
</script>



<style scoped>
.invisible {
  visibility: hidden;
}
</style>
