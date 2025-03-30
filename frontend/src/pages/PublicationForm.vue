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
import { defineComponent, reactive, toRefs } from 'vue'
import { Costs, Journal, Licence, Publication, Publisher } from 'components/models'
import { useRoute } from 'vue-router'
import { fetchPublication } from 'src/defs/api'
import PublicationView from 'components/PublicationView.vue'
import { useCommonStore } from 'stores/common-store'


export default defineComponent({
  name: 'PublicationFormPage',
  components: {
    PublicationView
  },
  setup() {
    const route = useRoute()
    const state = reactive({
      publicationId: route.params.id as string,
      publication: {
        type: {},
        publisher: {} as Publisher,
        mainPublisher: {} as Publisher,
        journal: {} as Journal,
        licence: {} as Licence,
        costs: {} as Costs
      } as Publication,
    })

    const commonStore = useCommonStore()
    const labelForInstitution = commonStore.labelForInstitution
//    const {t} = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

//    const $q = useQuasar()

    fetchPublication(state.publicationId)
      .then(pub => {
        state.publication = pub
    })
      .catch(error => {
        console.log(error)
      })

    return {
      ...toRefs(state),
      labelForInstitution
    }
  }
})
</script>

<template>
  <q-page padding>
    <q-card>
      <q-card-section>
        <PublicationView :publication="publication" />
      </q-card-section>
    </q-card>
  </q-page>
</template>

<style>
</style>

