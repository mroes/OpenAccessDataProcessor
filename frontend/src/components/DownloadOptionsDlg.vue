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
  <q-dialog position="standard" ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-pa-md" style="width: 400px;">
      <q-bar>
        {{ $t('title_download_options') }}
        <q-space />
        <q-btn dense flat icon="close" v-close-popup>
          <q-tooltip class="bg-white text-primary">{{ $t('btn_close') }}</q-tooltip>
        </q-btn>
      </q-bar>

      <q-card-section>
        <div class="q-mb-md">
<!--          <div class="text-h6 q-mb-sm">{{ $t('group_title_1') }}</div> -->
          <q-checkbox v-model="options.multipleRows" :label="$t('multiple_pub_rows')">
            <q-tooltip class="text-body2">{{ $t('tt_multiple_pub_rows') }}</q-tooltip>
          </q-checkbox>
        </div>

        <div class="q-mb-md">
          <div class="text-h6 q-mb-sm">{{ $t('optional_fields') }}</div>
          <div class="row">
            <q-checkbox v-model="options.includePublicationId" :label="$t('publicationid')">
              <q-tooltip class="text-body2">{{ $t('tt_publicationid') }}</q-tooltip>
            </q-checkbox>
            <q-checkbox v-model="options.includeNativeIds" :label="$t('pubid_inst')">
              <q-tooltip class="text-body2">{{ $t('tt_pubid_inst') }}</q-tooltip>
            </q-checkbox>
            <q-checkbox v-model="options.includeMetaSources" :label="$t('metadatasources')">
              <q-tooltip class="text-body2">{{ $t('tt_metadatasources') }}</q-tooltip>
            </q-checkbox>
            <q-checkbox v-model="options.includeAuthor" :label="$t('authors')">
              <q-tooltip class="text-body2">{{ $t('tt_authors') }}</q-tooltip>
            </q-checkbox>
          </div>
        </div>
      </q-card-section>

    </q-card>
  </q-dialog>
</template>

<script lang="ts">

import { ExcelDownloadOptions } from 'components/models'
import { useDialogPluginComponent } from 'quasar'
import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'DownloadOptionsDlg',
  components: {},
  props: {
    downloadOptions: {
      type: Object as () => ExcelDownloadOptions,
      required: true
    }
  },
  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // REQUIRED; must be called inside of setup()
    const { dialogRef, onDialogOK } = useDialogPluginComponent()
    // dialogRef      - Vue ref to be applied to QDialog
    // onDialogHide   - Function to be used as handler for @hide on QDialog
    // onDialogOK     - Function to call to settle dialog with "ok" outcome
    //                    example: onDialogOK() - no payload
    //                    example: onDialogOK({ /*.../* }) - with payload
    // onDialogCancel - Function to call to settle dialog with "cancel" outcome

    // eslint-disable-next-line vue/no-setup-props-destructure

    const options = ref<ExcelDownloadOptions>(Object.assign({}, props.downloadOptions))

    const onDialogHide = () => {
      // settingsStore.setStatus(options.value)
      onDialogOK(options)
    }

    return {
      // This is REQUIRED;
      // Need to inject these (from useDialogPluginComponent() call)
      // into the vue scope for the vue html template
      dialogRef,
      onDialogHide,
      options
    }
  }
})
</script>

<style scoped>
</style>
