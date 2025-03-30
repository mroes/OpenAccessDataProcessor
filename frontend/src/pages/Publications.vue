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
  <div v-if="tableview">
    <div class="text-h6">{{ $t('publications') }}
      <InfoButton text-id="publications_text1" />
    </div>

    <PublicationFilterView :options="filterOptions" @action="action" ref="filterPanel"></PublicationFilterView>

    <q-table
        ref="grid"
        class="my-sticky-header-table"
        :rows="rows"
        :columns="option.cols"
        row-key="id"
        :pagination="option.initialPagination"
        :rows-per-page-options="[0]"
        @row-click="openForm"
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
      </q-table>
    </div>
    <div v-else>
      {{ publication.title }}
    </div>

    <q-dialog v-model="downloadDlg" seamless position="bottom">
      <q-card style="width: 350px;background-color: #21BA45">
        <q-card-section class="row items-center no-wrap">
          <div>
            <div class="text-weight-bold">{{ $t('download_started') }}</div>
          </div>

          <q-space />

          <q-btn flat round icon="close" v-close-popup />
        </q-card-section>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, ref, toRefs } from 'vue'
import { apiExecuteCommand, fetchPublications } from 'src/defs/api'
import {
  DownloadRequest,
  Publication,
  PublicationFilter,
  PublicationFilterOptions,
  PublicationHeader,
  WebSocketMessage
} from 'components/models'
import { useI18n } from 'vue-i18n'
import PublicationFilterView from 'components/PublicationFilterView.vue'
import { useQuasar } from 'quasar'
import { openReport } from 'src/defs/common/openreport'
import PublicationDlg from 'components/PublicationDlg.vue'
import { QuasarTable } from 'components/QuasarModels'
import { useRoute } from 'vue-router'
import { useCommonStore } from 'stores/common-store'
import SocketService from 'src/defs/socketService'
import InfoButton from 'components/InfoButton.vue'
import { ConnectParams, useSocketService } from 'src/composables/useSocketService'

export default defineComponent({
  name: 'PublicationsPage',
  components: {
    InfoButton,
    PublicationFilterView
  },
  setup() {
    // the route is needed for the query parameters
    const route = useRoute()

    const commonStore = useCommonStore()

    const userQueueCallback = (message : WebSocketMessage) => {
      // console.log('Message received:', message.cmd);
      if (message.cmd === 'download_ready') {
        const key = message.data as unknown as string
        downloadPublications({ key: key })
      }
    }


    const { runtimeConfig } = useSocketService( {
      userQueueCallback: userQueueCallback,
      onAfterConnect: () => { apiExecuteCommand('state') }
    } as ConnectParams)

    const state = reactive({
      tableview: true,
      publication: {} as Publication,
      filterOptions: {
        color: route.query.color,
        institutionId: route.query.institutionId,
        disableDownloadButton: false
      } as PublicationFilterOptions,
      downloadDlg: false,
      downloadUseWebSockets: runtimeConfig?.downloadUseWs,
    })

    const grid = ref<QuasarTable>()
    const {t} = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const pubTypeLabel = (val: number) => {
      return commonStore.labelForPublicationType(val)
    }

    const fixedColumns = [
      {
        name: 'title',
        required: true,
        label: t('title'),
        align: 'left',
        field: 'title',
        sortable: true
      },
      {
        name: 'year',
        required: true,
        label: t('year'),
        align: 'left',
        field: 'year',
        format: (val: never) => `${val}`,
        sortable: true
      },
      {
        name: 'pubtypeId',
        required: true,
        label: t('pubtype'),
        align: 'left',
        field: 'pubtypeId',
        format: (val: never) => pubTypeLabel(val),
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
        name: 'color',
        required: true,
        label: t('color'),
        align: 'left',
        field: 'color',
        format: (val: never) => val,
        sortable: true
      },
      {
        name: 'publisher',
        required: true,
        label: t('publisher'),
        align: 'left',
        field: 'publisher',
        sortable: true,
        style: 'max-width: 200px',
        classes: 'overflow'
      },
      {
        name: 'mainpublisher',
        required: true,
        label: t('mainpublisher'),
        align: 'left',
        field: 'mainPublisher',
        sortable: true,
        style: 'max-width: 200px',
        classes: 'overflow'
      }
    ]

    const initialPagination = {
        sortBy: 'desc',
        descending: false,
        page: 0,
        rowsPerPage: 0
    }

    const $q = useQuasar()

    const rows = ref([] as Array<PublicationHeader>)

    const option = computed( () => {
      return {
        initialPagination,
        rows : rows,
        cols : fixedColumns
      }
    })

    function loadPublications(filter: PublicationFilter) {
      const startTime = Date.now()
      fetchPublications(filter).then(pubs => {
        // clear out existing data and add new
        try {
          const len = rows.value.length
          console.log('got result: time = ', Date.now() - startTime, ' prev size = ' ,len, ', new size = ', pubs.length)
          let maxLen = pubs.length
          // the amount of publications is limited to avoid problems in the browser
          if (maxLen > 5000) {
            maxLen = 5000
            $q.notify({
              type: 'warning',
              message: t('msg_results_are_restricted', [pubs.length, maxLen])
            })
          }
          rows.value = pubs.slice(0, maxLen)
        } catch (e) {
          console.error(e)
        } finally {
          $q.loading.hide()
        }
      }).catch(error => {
        $q.notify({
          type: 'negative',
          timeout: 20000,
          message: error.message
        })
        $q.loading.hide()
      })

    }

    function startDownload(filter: PublicationFilter) {
      const msg = {
        cmd: 'download',
        data: filter
      }
      SocketService.send(msg)
      state.downloadDlg = true
      state.filterOptions.disableDownloadButton = true
      $q.loading.hide()
    }

    function downloadPublications(data: DownloadRequest) {
      openReport('publications', data)
        .catch(response => {
          let msg = ''
          if (response.messages && response.messages.length > 0) {
            response.messages.forEach((m: string) => {
              msg += m
            })
          }
          if (response.errorMsg && response.errorMsg.length > 0) {
            msg = response.errorMsg
          }
          if (msg === '') {
            msg = 'Unknown error!'
          }
          $q.notify({
            message: msg,
            type: 'negative',
            timeout: 0,
            actions: [
              { icon: 'close', color: 'white', round: true, handler: () => { /* */ } }
            ]
          })
        })
        .finally( () => {
          $q.loading.hide()
          state.downloadDlg = false
          state.filterOptions.disableDownloadButton = false
//          filterPanel.value?.disableDownloadButton(false)
        })

    }

    const action = ( params : { cmd: string, filter : PublicationFilter}) => {
        $q.loading.show(
          {delay: 10}
        )
        if (params.cmd === 'search') {
          loadPublications(params.filter)
        } else {
          if (state.downloadUseWebSockets && SocketService.isConnected()) {
            startDownload(params.filter)
          } else {
            downloadPublications({ filter: params.filter })
          }
        }
    }

    const openForm = (event : never, row : Publication, index: number) => {
      $q.dialog({
        component: PublicationDlg,
        componentProps: {
          publicationId: row.id,
          publicationIndex: index,
          rows: grid.value ? grid.value.filteredSortedRows : []
        }
      })
    }

    return {
      ...toRefs(state),
      grid,
      rows,
      option,
      action,
      openForm
    }
  }
})
</script>

<style>
.title  {
  max-width: 250px;
}
.overflow  {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.overflow:hover  {
  overflow: visible;
  background-color: white;
  white-space: normal;
  height:auto;
}

</style>

<style lang="sass">
.my-sticky-header-table
  /* height or max-height is important */
  height: calc(100vh - 330px)

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
