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
  <q-page padding>
    <div v-if="hasRole">
      <div class="row" style="margin-bottom: 20px">
        <q-space></q-space>
        <div class="bordered" style="margin-left: 0px; min-width:300px; max-width: 400px">
          {{ $t('lbl_processingState') }}: {{ processingState }}
        </div>
      </div>
      <div class="row q-col-gutter-md">
        <div class="col-4">
          <q-btn @click="executeCommand('updatePublishers')"
                 :label="$t('updatePublishers')"
                 :loading="loading['updatePublishers']"
                 no-caps
          ></q-btn>

          <p></p>
          <q-btn @click="executeCommand('resetUnknownPublisherFlag')"
                 :label="$t('resetUnknownPublisherFlag')"
                 :loading="loading['resetUnknownPublisherFlag']"
                 no-caps
          ></q-btn>

        </div>
        <div class="col-4">
          <q-btn @click="executeCommand('collect')"
                 :label="$t('collectPubs')"
                 :loading="loading['collect']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('ingest')"
                 :label="$t('ingest')"
                 :loading="loading['ingest']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('fetch')"
                 :label="$t('fetchPubs')"
                 :loading="loading['fetch']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('update')"
                 :label="$t('updatePubs')"
                 :loading="loading['update']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('classify')"
                 :label="$t('classify')"
                 :loading="loading['classify']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('identifyJournals')"
                 :label="$t('identifyJournals')"
                 :loading="loading['identifyJournals']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('identifyPublishers')"
                 :label="$t('identifyPublishers')"
                 :loading="loading['identifyPublishers']"
                 no-caps
          ></q-btn>
        </div>
        <div class="col-4">
          <q-btn @click="executeCommand('createElasticIndex')"
                 :label="$t('createElasticIndex')"
                 :loading="loading['createElasticIndex']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('updateElasticData')"
                 :label="$t('updateElasticData')"
                 :loading="loading['updateElasticData']"
                 no-caps
          ></q-btn>
          <p></p>

          <q-btn @click="executeCommand('clearCache')"
                 :label="$t('clearCache')"
                 :loading="loading['clearCache']"
                 no-caps
          ></q-btn>
        </div>
      </div>

      <p></p>

      <div class="row q-gutter-sm" style="align-items: center;">
        <q-input
          v-model="consoleInput"
          style="flex: 1;"
          :label="$t('commandInput')"
          filled
          dense
          @keydown.enter="executeConsoleCommand"
        />
        <q-btn
          :label="$t('btn_execute')"
          color="primary"
          @click="executeConsoleCommand"
          no-caps
        />
      </div>


    </div>

    <div v-else>
      {{ $t('msg_please_login')}}
    </div>

  </q-page>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue'
import { apiExecuteCommand } from 'src/defs/api'
import Role from 'src/defs/role'
import { useAuthService } from 'src/composables/useAuthService'
import { WebSocketMessage } from 'components/models'
import { ConnectParams, useSocketService } from 'src/composables/useSocketService'

export default defineComponent({
  name: 'AdminPage',
  setup() {

    type CmdKeys =
      'state' |
      'collect' |
      'ingest' |
      'classify' |
      'fetch' |
      'update' |
      'identifyJournals' |
      'updatePublishers' |
      'identifyPublishers' |
      'createElasticIndex' |
      'updateElasticData' |
      'resetUnknownPublisherFlag' |
      'clearCache'

    const state = reactive({
      consoleInput: '',
      loading: {
        collect: false,
        ingest: false,
        classify: false,
        fetch: false,
        update: false,
        identifyJournals: false,
        updatePublishers: false,
        identifyPublishers: false,
        createElasticIndex: false,
        updateElasticData: false,
        resetUnknownPublisherFlag: false,
        clearCache: false
      } as Record<CmdKeys, boolean>
    })


    const { hasRole } = useAuthService();

    const topicCallback = (message : WebSocketMessage) => {
      if (message.cmd === 'process_state') {
        console.log('msg received ' + message.data)
        processingState.value = message.data as unknown as string
      }
    }

    const { processingState } = useSocketService( {
        topicCallback : topicCallback,
        onAfterConnect: () => executeCommand('state')
      } as ConnectParams)

    const executeCommand = (cmd: CmdKeys) => {
      console.log('start cmd ' + cmd)
      state.loading[cmd] = true
      apiExecuteCommand(cmd)
        .finally( () => {
          state.loading[cmd] = false
        })
    }

    const executeConsoleCommand = () => {
      if (!state.consoleInput.trim())
        return
      console.log('Executing: ' + state.consoleInput)
      apiExecuteCommand(state.consoleInput.trim())
        .then(() => console.log('Command sent successfully'))
        .catch((err: Error) => console.error('Error executing command', err))
        .finally(() => state.consoleInput = '')
    }

    return {
      ...toRefs(state),
      processingState,
      hasRole: hasRole(Role.admin),
      executeCommand,
      executeConsoleCommand
    }
  }
})
</script>
