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
  <q-dialog position="standard" ref="dialogRef" @hide="onDialogHide" >
    <q-card style="width: 900px; max-width: 80vw;">
      <q-bar>
        <q-space />
        <q-btn v-if="hasMultipleRows()" dense flat icon="arrow_back" @click="prev" >
          <q-tooltip class="bg-white text-primary">{{ $t('btn_prev') }}</q-tooltip>
        </q-btn>
        <q-btn v-if="hasMultipleRows()" dense flat icon="arrow_forward" @click="next" >
          <q-tooltip class="bg-white text-primary">{{ $t('btn_next') }}</q-tooltip>
        </q-btn>
        <q-btn dense flat icon="close" v-close-popup>
          <q-tooltip class="bg-white text-primary">{{ $t('btn_close') }}</q-tooltip>
        </q-btn>
      </q-bar>

      <q-card-section>
        <PublicationView :publication="publication" />
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">

import { Publication, PublicationHeader } from 'components/models'
import { useDialogPluginComponent, useQuasar } from 'quasar'
import { defineComponent, reactive, toRefs } from 'vue'
import PublicationView from 'components/PublicationView.vue'
import { fetchPublication } from 'src/defs/api'
import { useCommonStore } from 'stores/common-store'

export default defineComponent( {
  name: 'PublicationDlg',
  components: {
    PublicationView
  },
  props: {
    publicationId: {
      type: String,
      required: true
    },
    publicationIndex: {
      type: Number,
      required: false
    },
    rows: {
      type: Object as () => Array<PublicationHeader>,
      required: false
    }
  },
  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup (props) {
    // REQUIRED; must be called inside of setup()
    const {dialogRef} = useDialogPluginComponent()
    // dialogRef      - Vue ref to be applied to QDialog
    // onDialogHide   - Function to be used as handler for @hide on QDialog
    // onDialogOK     - Function to call to settle dialog with "ok" outcome
    //                    example: onDialogOK() - no payload
    //                    example: onDialogOK({ /*.../* }) - with payload
    // onDialogCancel - Function to call to settle dialog with "cancel" outcome

    // eslint-disable-next-line vue/no-setup-props-destructure

    let publicationIndex = props.publicationIndex as number

    const state = reactive({
      publication: {
        type: {},
        publisher: {},
        journal: {},
        licence: {},
        costs: {}
      } as Publication,
    })

    const commonStore = useCommonStore()
    const labelForInstitution = commonStore.labelForInstitution
//    const {t} = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const $q = useQuasar()

    const loadPublication = (publicationId: string) => {
      $q.loading.show(
        {delay: 10}
      )
      fetchPublication(publicationId)
        .then(pub => {
          state.publication = pub
        })
        .catch(error => {
          console.log(error)
        })
        .finally( () => {
          $q.loading.hide()
        })
    }


    const onDialogHide = () => {
      return
    }

    const hasMultipleRows = () => {
      return props.rows && props.rows.length > 1
    }

    const next = () => {
      if (props.rows) {
        publicationIndex++
        if (publicationIndex >= props.rows.length) {
          publicationIndex = 0
        }
        const activePup = props.rows[publicationIndex]
        loadPublication(activePup.id)
      }
    }

    const prev = () => {
      if (props.rows) {
        publicationIndex--
        if (publicationIndex < 0) {
          publicationIndex = props.rows.length-1
        }
        const activePup = props.rows[publicationIndex]
        loadPublication(activePup.id)
      }
    }

    loadPublication(props.publicationId)

    return {
      // This is REQUIRED;
      // Need to inject these (from useDialogPluginComponent() call)
      // into the vue scope for the vue html template
      ...toRefs(state),
      labelForInstitution,
      dialogRef,
      hasMultipleRows,
      prev,
      next,
      onDialogHide
    }
  }
})
</script>

<style scoped>

</style>
