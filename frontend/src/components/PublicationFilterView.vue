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

<script lang="ts">
import { computed, defineComponent, nextTick, reactive, toRefs, watch } from 'vue'
import { Publication, PublicationFilterOptions } from 'components/models'
import { ALL_INSTITUTIONS } from 'src/defs/const'
import { buildInstitutionOptions, buildPublicationTypeOptions } from 'src/defs/options_support'
import { useI18n } from 'vue-i18n'
import { useCommonStore } from 'stores/common-store'
import SelectPublisher from 'components/SelectPublisher.vue'
import DownloadOptionsDlg from 'components/DownloadOptionsDlg.vue'
import { useQuasar } from 'quasar'
import { useSettingsStore } from 'stores/settings-store'


export default defineComponent({
  name: 'PublicationFilterView',
  components: { SelectPublisher },
  provide: {},
  props: {
    options: {
      type: Object as () => PublicationFilterOptions,
      required: false
    }
  },
  emits: ['action'],
  setup(props, context) {

    const $q = useQuasar()
    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const commonStore = useCommonStore()
    const settingsStore = useSettingsStore()

    const oaColors = commonStore.getAllOAColors

    const actualYear = new Date().getFullYear()
    const state = reactive({
      form: {
        publication: null as unknown as Publication,
        doi: '',
        startYear: actualYear,
        endYear: actualYear,
        selectedInstitution: props.options ? props.options.institutionId : ALL_INSTITUTIONS,
        publicationType: 0,
        color: props.options?.color,
        title: '',
        author: '',
        publisherId: '',
        publisher: '',
        publisherExact: false,
        journal: '',
        filterMainPublisher: false,
        includeUnknown: false
      },
      downloadOptions: settingsStore.getExcelDownloadOptions,
      disableDownload: props.options?.disableDownloadButton
    })

    watch( () => props.options?.disableDownloadButton, (newValue /*, oldValue */) => {
      // console.log('Prop "options" changed:', newValue, oldValue);
      state.disableDownload = newValue
    })

    const publishers = computed(() => {
      const sortedPublishers = commonStore.getPublishers
      if (sortedPublishers) {
        return sortedPublishers
      }
      return []
    })

    const updateYear = async () => {
      await nextTick()
    }

    const action = (cmd: string) => {
      context.emit('action',
        {
          cmd: cmd,
          filter: {
            institution: state.form.selectedInstitution,
            year: state.form.startYear,
            color: state.form.color,
            doi: state.form.doi,
            title: state.form.title,
            author: state.form.author,
            publisher: state.form.publisher,
            publisherId: state.form.publisherId,
            publisherExact: state.form.publisherExact,
            journal: state.form.journal,
            filterMainPublisher: state.form.filterMainPublisher,
            includeUnknown: state.form.includeUnknown,
            options: state.downloadOptions
          }
        })
    }

    const disableDownloadButton = (value : boolean) => {
      state.disableDownload = value
    }

    context.expose({ disableDownloadButton })

    const showOptionsDlg = () => {
      $q.dialog({
        component: DownloadOptionsDlg,
        componentProps: {
          downloadOptions: state.downloadOptions
        }
      }).onOk((options) => {
        state.downloadOptions = options
        settingsStore.setExcelDownloadOptions(options)
      })
    }

    return {
      ...toRefs(state),
      institutionOptions: buildInstitutionOptions(t),
      publishers,
      publicationTypeOptions : buildPublicationTypeOptions(),
      oaColors,
      updateYear,
      action,
      disableDownloadButton,
      showOptionsDlg
    }
  }
})
</script>

<template>
  <div class="row bordered" style="margin-left: 0px;margin-right: 0px;margin-bottom: 10px">
    <div class="q-pa-xs">
      <q-input v-model="form.doi" outlined :label="$t('doi')"
               clearable
               class="width200"
      />
    </div>
    <div class="q-pa-xs">
      <q-input v-model="form.title" outlined :label="$t('title')"
               clearable
               class="width200"
      />
    </div>

    <div class="q-pa-xs">
      <q-input v-model="form.author" outlined :label="$t('author')"
               clearable
               class="width200"
               :placeholder="$t('placeholder_author')"
      />
    </div>

    <div class="q-pa-xs">
      <q-input v-model="form.startYear" outlined :label="$t('year')" type="number"
               ref="startYearRef"
               :rules="[
                 /* val => !!val || $t('err_field_required'), */
                   val => val ? val > 2000 || $t('err_min_date', [2000]) : true
                   ]"
               @update:model-value="updateYear"
               class="width200"
      />
    </div>

    <div class="q-pa-xs">
      <q-select v-model="form.color"
                outlined  :options="oaColors" :label="$t('color')"
                option-value="name"
                option-label="name"
                emit-value
                map-options
                clearable
                class="width200"
      />
    </div>

    <div class="q-pa-xs">
      <q-select v-model="form.selectedInstitution"
                outlined  :options="institutionOptions" :label="$t('institution')" style="width: 250px;"
                option-value="id"
                option-label="name"
                emit-value
                map-options
                class=""
      />
    </div>

    <div class="q-pa-xs">
      <SelectPublisher
        v-model="form.publisherId"
        style="width: 250px"
        :publishers="publishers"
      >
      </SelectPublisher>

    </div>

    <div class="q-pa-xs">
      <q-toggle
        v-model="form.filterMainPublisher"
        :label="$t('mainpublisher')"
      >
        <q-tooltip class="text-body2">{{ $t('tt_filterMainPublisher') }}</q-tooltip>
      </q-toggle>
    </div>

    <div class="q-pa-xs">
      <q-toggle
        v-model="form.includeUnknown"
        :label="$t('showUnknown')"
      >
        <q-tooltip class="text-body2">{{ $t('tt_includeUnknown') }}</q-tooltip>
      </q-toggle>
    </div>

    <div class="q-pa-xs">
      <q-btn color="primary" :label="$t('btn_search')" style="height: 20px"
             @click="action('search')"
             class=""
      />
    </div>

    <div class="q-pa-xs">
      <q-btn color="primary" :label="$t('btn_download')" style="height: 20px"
             @click="action('download')"
             class=""
             :disable="disableDownload"
      />
    </div>

    <div class="q-pa-xs">
      <q-btn flat round icon="fa-solid fa-gear" @click="showOptionsDlg" style="height: 20px" />
    </div>

    <p></p>
  </div>

</template>

<style scoped>
.width200 {
  width: 200px;
}
</style>
