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
import { computed, defineComponent, reactive, Ref, ref, toRefs } from 'vue'
import { useI18n } from 'vue-i18n'
import { QUploader, useQuasar } from 'quasar'
import { useCommonStore } from 'stores/common-store'
import { PublicationSource, UploadResponse, WebSocketMessage } from 'components/models'
import { apiExecuteCommand, importPublications } from 'src/defs/api'
import tokenStoreSessionStorage from 'src/defs/tokenstore'
import Role from 'src/defs/role'
import { useAuthService } from 'src/composables/useAuthService'
import { ConnectParams, useSocketService } from 'src/composables/useSocketService'
import markdownItSup from 'markdown-it-sup'
import markdownItLinkAttributes from 'markdown-it-link-attributes'
import { VueMarkdownIt } from '@f3ve/vue-markdown-it'

function extractRorId(name: string): string {
  const idxUnderScore = name.indexOf('_')
  if (idxUnderScore < 0) {
    return name
  }
  return name.substring(0, idxUnderScore).toLowerCase();
}

export default defineComponent({
  name: 'UploadPublicationsPage',
  components: {VueMarkdownIt},
  setup() {

    const $q = useQuasar()
    const {t} = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    type CmdKeys =
      'state' |
      'import' |
      'ingest'

    const state = reactive({
      firstPage: true,
      selectedInstitution: '',
      fileName: '',
      uploadUrl: 'api/publication/upload',
      uploadError: '',
      authToken: tokenStoreSessionStorage.load(),
      uploading: false,
      result: {} as UploadResponse,
      loading: {
        import: false,
        ingest: false
      } as Record<CmdKeys, boolean>,
      disabled: {
        import: false,
        ingest: false
      } as Record<CmdKeys, boolean>
    })

    const commonStore = useCommonStore()
    const { hasRole } = useAuthService();
    const topicCallback = (message : WebSocketMessage) => {
      if (message.cmd === 'process_state') {
        processingState.value = message.data as unknown as string
      }
    }

    const plugins = [markdownItSup,
      [markdownItLinkAttributes, {
        attrs: {
          target: '_blank',
          rel: 'noopener',
        }
      }]
    ]

    const { processingState } = useSocketService( {
      topicCallback : topicCallback,
      onAfterConnect: () => { apiExecuteCommand('state') }
    } as ConnectParams)

    const uploader = ref() as Ref<QUploader>

    const uploadFunc = () => {
      state.uploadError = ''
      if (!state.authToken) {
        state.uploadError = t('msg_enter_authtoken')
        return
      }
      const files = uploader.value.files
      if (files && files.length > 0) {
        let msg = ''
        // check ROR id's in file names
        files.forEach(file => {
          const rorId = extractRorId(file.name)
          if (!commonStore.isExistingRor(rorId)) {
            if (msg) {
              msg += ', '
            }
            msg += rorId
          }
        })
        if (msg) {
          state.uploadError = t('err_missing_ror_prefix') + ': ' + msg
          return
        }
        uploader.value.upload()
      } else {
        state.uploadError = t('msg_choose_file')
        return
      }
    }

    const failedFunc = ({ xhr }: { xhr: XMLHttpRequest }) => {
      state.uploading = false
      let errorText = xhr.response
      if (!errorText) {
        errorText = xhr.statusText
      }
      state.uploadError = t('lbl_error') + ': ' + errorText

    }

    const uploadedFunc = ({ xhr }: { xhr: XMLHttpRequest }) => {
      state.firstPage = false
      state.uploading = false
      state.uploadError = ''
      state.result = JSON.parse(xhr.response)
      state.selectedInstitution = state.result.institutionId
      let previewRows = state.result.fetchResult.publicationSources
      if (!previewRows) {
        previewRows = []
      }
      rows.value = previewRows
    }

    // Preview

    const rows = ref([] as Array<PublicationSource>)

    const fixedColumns = [
      {
        name: 'nativeId',
        required: true,
        label: t('nativeid'),
        align: 'left',
        field: 'nativeId',
        sortable: true
      },
      {
        name: 'title',
        required: true,
        label: t('title'),
        align: 'left',
        field: 'title',
        style: 'max-width: 200px',
        headerStyle: 'max-width: 200px',
        sortable: true
      },
      {
        name: 'pubtype',
        required: true,
        label: t('pubtype'),
        align: 'left',
        field: 'pubtype',
        sortable: true
      },
      {
        name: 'doi',
        required: true,
        label: t('doi'),
        align: 'left',
        field: 'doi',
        sortable: true
      },
      {
        name: 'record',
        required: true,
        label: t('fields'),
        align: 'left',
        field: 'record',
        style: 'max-width: 200px',
        headerStyle: 'max-width: 200px',
      }
    ]

    const initialPagination = {
      sortBy: 'desc',
      descending: false,
      page: 0,
      rowsPerPage: 0
    }

    const option = computed( () => {
      return {
        initialPagination,
        rows : rows,
        cols : fixedColumns
      }
    })

    const parseJSON = (jsonString : string) => {
      try {
        return JSON.stringify(JSON.parse(jsonString), null, 2); // Pretty print JSON
      } catch (e) {
        return 'Invalid JSON';
      }
    }

    const importPublicationsFunc = (cmd: CmdKeys) => {
      state.loading[cmd] = true;
      importPublications({
        cmd: cmd,
        key: state.result.key
      }).then( () => {
        $q.notify({
          type: 'positive',
          message: t('msg_publications_imported')
        })
      }).catch( (error => {
        $q.notify({
          type: 'positive',
          message: error.message
        })
      }))
        .finally( () => {
          state.disabled.import = true;
          state.disabled.ingest = true;
          state.loading[cmd] = false;
      })
    }

    const newUpload = () => {
      state.disabled.import = false;
      state.disabled.ingest = false;
      state.firstPage = true
      state.selectedInstitution = ''
    }

    return {
      ...toRefs(state),
      processingState,
      plugins,
      uploader,
      institutionOptions: commonStore.getInstitutionsWithFileDelivery,
      uploadFunc,
      failedFunc,
      uploadedFunc,
      labelForInstitution: commonStore.labelForInstitution,
      option,
      rows,
      parseJSON,
      importPublicationsFunc,
      newUpload,
      hasRole: hasRole(Role.upload)
    }
  }
})
</script>

<template>
  <q-page padding>
    <div v-if="hasRole">
      <div class="row">
    <div class="text-h6">{{ $t('title_update_publications') }} <span v-if="selectedInstitution"> {{ labelForInstitution(selectedInstitution) }}</span></div>
        <q-space></q-space>
      <div class="bordered" style="margin-left: 0px; min-width:300px; max-width: 400px">
        {{ $t('lbl_processingState') }}: {{ processingState }}
      </div>
      </div>
<p></p>
    <div v-if="firstPage">
      <p><vue-markdown-it :source="$t('txt_upload')" :plugins="plugins" /></p>

      <div class="col">

        <div class="row">
          <q-uploader ref="uploader"
                      :label="$t('publication_filename')"
                      :url="uploadUrl"
                      :multiple="false"
                      field-name="file"
                      :headers="[{name: 'authorization', value: `Bearer ${authToken}`}]"
                      :form-fields="[{'name': 'name', 'value': fileName}]"
                      extensions="*.xls,*.xlsx"
                      :error-message="$t('err_invalid_file')"
                      accept=".xls,.xlsx"
                      hide-upload-btn
                      style="margin-bottom: 20px"
                      @failed="failedFunc"
                      @uploaded="uploadedFunc"
          >

          </q-uploader>
        </div>

        <div class="row error">{{ uploadError }}
        </div>

        <div class="row" style="min-width: 200px">
          <q-btn-group>
            <q-btn :label="$t('btn_upload')"
                   @click="uploadFunc"
                   :no-caps="true"
            />
          </q-btn-group>
        </div>
      </div>

    </div>
    <div v-else>
      <p>
        {{ $t('lbl_fetch_result', [result.fetchResult.importedRecords, result.fetchResult.createdRecords,
        result.fetchResult.modifiedRecords, result.fetchResult.unchangedRecords, result.fetchResult.ignoredRecords]) }}
      </p>
      <p></p>

      <q-table
        ref="grid"
        class="my-sticky-header-table"
        :rows="rows"
        :columns="option.cols"
        row-key="id"
        :pagination="option.initialPagination"
        :rows-per-page-options="[0]"
        virtual-scroll
      >
        <template v-slot:body-cell-title="props">
          <q-td :props="props" class="title ellipsis"
          >
            <span>{{ props.value }}
            <q-tooltip class="text-body2" :offset="[10, 10]">
              {{ props.value }}
            </q-tooltip>
            </span>
          </q-td>
        </template>
        <template v-slot:body-cell-record="props">
          <q-td :props="props" class="title ellipsis"
          >
            <span>{{ props.value }}
            <q-tooltip class="text-body2" :offset="[10, 10]" >
              <div style="max-width: 600px"> {{  parseJSON(props.value) }} </div>
            </q-tooltip>
            </span>
          </q-td>
        </template>
      </q-table>

      <p></p>
    <div class="row" style="min-width: 200px">
      <div class="q-pa-md q-gutter-sm">
        <q-btn :label="$t('btn_import_pubs')"
               @click="importPublicationsFunc('import')"
               :loading="loading['import']"
               :disable="disabled['import']"
               :no-caps="true"
        />
        <q-btn :label="$t('btn_ingest_pubs')"
               @click="importPublicationsFunc('ingest')"
               :loading="loading['ingest']"
               :disable="disabled['ingest']"
               :no-caps="true"
        />
        <q-btn :label="$t('btn_new_upload')"
               @click="newUpload"
               :no-caps="true"
        />
      </div>
    </div>

    </div>
    </div>
    <div v-else>
      {{ $t('msg_please_login')}}
    </div>
  </q-page>
</template>

<style scoped>
.error { color: red}
</style>

<style lang="sass">
.my-sticky-header-table
  /* height or max-height is important */
  height: calc(100vh - 300px)

  .q-table__top,
  .q-table__bottom,
  thead tr:first-child th
    /* bg color is important for th; just specify one */
    background-color: white /* #00b4ff */

  thead tr th
    position: sticky
    z-index: 1
  thead tr:first-child th
    top: 0

  /* this is when the loading indicator appears */
  &.q-table--loading thead tr:last-child th
    /* height of all previous header rows */
    top: 48px

  /* prevent scrolling behind sticky top row on focus */
  tbody
    /* height of all previous header rows */
    scroll-margin-top: 48px
</style>
