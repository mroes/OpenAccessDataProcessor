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
    <div class="text-h6">{{ $t('classification_publication') }}
      <InfoButton text-id="coat_text1" />
    </div>

    <div class="row items-center" >
      <div class="col" style="max-width: 300px">
      <q-input
        outlined
        v-model="doi"
        :label="$t('doi')"
        bottom-slots
        :error="isError"
        :error-message="doiError"
        @keyup.enter.prevent="classify"
      />
      </div>
      <div class="col">
        &nbsp;
        <q-btn @click="classify"
               :label="$t('search')"
               :loading="searching"
        ></q-btn>
      </div>
    </div>
    <div v-if="publication">
      <table>
        <tbody>
        <tr>
          <td style="min-width: 120px">DOI</td>
          <td style="width: 5px">&nbsp;</td>
          <td>{{ doi }}</td>
        </tr>
        <tr>
          <td>Id</td>
          <td>&nbsp;</td>
          <td>{{ publication.id }}</td>
        </tr>
        <tr>
          <td>Titel</td>
          <td>&nbsp;</td>
          <td>{{ publication.title }}</td>
        </tr>
        <tr>
          <td>Klassifizierung</td>
          <td>&nbsp;</td>
          <td>{{ coat.place + ', ' + coat.licence + ', ' + coat.version + ', ' + coat.embargo + ', ' + coat.conditions}}</td>
        </tr>
        <tr>
          <td>Farbe</td>
          <td>&nbsp;</td>
          <td>{{ coat.name }}</td>
        </tr>
        <tr>
          <td>Farbe Unpaywall</td>
          <td>&nbsp;</td>
          <td>{{ publication.colorUpw }}</td>
        </tr>
        </tbody>
      </table>
      <p></p>
      <div class="row" style="border-bottom: solid 1px black">
        <div class="col">
          {{ $t('criteria') }}
        </div>
        <div class="col">
          {{ $t('coat_value') }}
        </div>
        <div class="col">
          {{ $t('coat_description') }}
        </div>
        <div class="col">
          {{ $t('coat_source') }}
        </div>
      </div>
      <div class="row" v-for="field in coatDescription.keys" :key = field >
        <div class="col">
          {{ $t('coat' + field) }}
        </div>
        <div class="col">
          {{ coat[field] }}
        </div>
        <div class="col">
          {{ coatDescription[field][coat[field]] }}
        </div>
        <div class="col">
          {{ coat[sourceField(field)] }}
        </div>
      </div>
      <p></p>
      Quellen: {{ sources2String(coat.sources) }}

    <p></p>
    <hr>
      <span style="font-size: medium"> {{ $t('classification_publication_details') }} </span>

    <p></p>

    <div class="row" v-for="step in steps" :key = step >
      {{ step }}<br>
    </div>

    </div>

    <ObjectVisualizer
      v-if=hasData(crossref)
      :data=crossref
      rootName="crossref"
      :expandOnCreatedAndUpdated="(path) => false"
      :getKeys="(object, path) => Object.keys(object)"
    ></ObjectVisualizer>

    <ObjectVisualizer
      v-if=hasData(unpaywall)
      :data=unpaywall
      rootName="unpaywall"
      :getKeys="(object, path) => Object.keys(object)"
    ></ObjectVisualizer>

    <ObjectVisualizer
      v-if=hasData(doaj)
      :data=doaj
      rootName="doaj"
      :getKeys="(object, path) => Object.keys(object)"
    ></ObjectVisualizer>

    <ObjectVisualizer
      v-if=hasData(openapc)
      :data=openapc
      rootName="openapc"
      :getKeys="(object, path) => Object.keys(object)"
    ></ObjectVisualizer>

    <ObjectVisualizer
      v-if=hasData(romeo)
      :data=romeo
      rootName="romeo"
      :getKeys="(object, path) => Object.keys(object)"
    ></ObjectVisualizer>

    <ObjectVisualizer
      v-if=hasData(openalex)
      :data=openalex
      rootName="openalex"
      :getKeys="(object, path) => Object.keys(object)"
    ></ObjectVisualizer>

  </q-page>
</template>

<script lang="ts">
import {computed, defineComponent, reactive, toRefs} from 'vue'
import {required} from '@vuelidate/validators'
import useVuelidate from '@vuelidate/core'
import {useI18n} from 'vue-i18n'
import {classifyPublication} from 'src/defs/api'
import {Coat, coatDescription, Publication} from 'components/models'
import {checkDoi} from 'src/defs/doisupport'
import { ObjectVisualizer } from 'object-visualizer';
import 'object-visualizer/dist/index.min.css'
import {isObjectEmpty} from 'src/defs/common'
import { useRoute } from 'vue-router'
import InfoButton from 'components/InfoButton.vue'

function sourceField(field: string) : string {
  const result = 'src' + field.charAt(0).toUpperCase() + field.substring(1)
  return result;
}

function sources2String(sources: number) : string {
  let result = ''
  if ((sources & 1) == 1) {
    result += 'Crossref'
  }
  if ((sources & 2) == 2) {
    if (result.length > 0) {
      result += ', '
    }
    result += 'Unpaywall'
  }
  if ((sources & 4) == 4) {
    if (result.length > 0) {
      result += ', '
    }
    result += 'Doaj'
  }
  if ((sources & 8) == 8) {
    if (result.length > 0) {
      result += ', '
    }
    result += 'OpenApc'
  }
  if ((sources & 16) == 16) {
    if (result.length > 0) {
      result += ', '
    }
    result += 'Sherpa/Romeo'
  }
  return result
}

function hasData(obj:unknown) :  boolean {
  return !!obj && !isObjectEmpty(obj)
  }

export default defineComponent({
  name: 'CoatClassifyPage',
  components: {
    InfoButton,
    ObjectVisualizer
  },
  setup() {
    const state = reactive({
      doi: '',
      publication: null as unknown as Publication,
      coat: null as unknown as Coat,
      steps: [] as Array<string>,
      crossref: {} as object,
      unpaywall: {} as object,
      doaj: {} as object,
      openapc: {} as object,
      romeo: {} as object,
      openalex: {} as object,
      serverError: false,
      searching: false,
    })

    let autoClassify = false
    const route = useRoute()
    if (route.query && route.query.doi) {
      if (Array.isArray(route.query.doi)) {
        // state.doi = route.query.doi[0]
      } else {
        state.doi = route.query.doi
        if (state.doi) {
          autoClassify = true
        }
      }
    }

    // eslint-disable-next-line @typescript-eslint/unbound-method
    const {t} = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const validation_rules = computed(() => ({
      doi: {
        required,
        checkDoi
      },
    }))

    const v$ = useVuelidate(validation_rules, state)

    const isError = computed(() => {
      const error = v$.value.doi.$error || state.serverError
      return error
    })

    const doiError = computed(() => {
      let message = ''
      v$.value.doi.$errors.forEach(error => {
        message += t(error.$validator)
      })
      if (message === '' && state.serverError) {
        message = t('err_doi_not_found')
      }
      return message
    })


    const classify = async () => {

      const result = await v$.value.$validate()
      if (!result) {
        return
      }

      state.searching = true
      classifyPublication(state.doi)
        .then((result) => {
          state.publication = result.publication
          state.coat = result.coat
          state.steps = result.explanation.steps
          state.crossref = result.explanation.crossref
          state.unpaywall = result.explanation.unpaywall
          state.doaj = result.explanation.doaj
          state.openapc = result.explanation.openapc
          state.romeo = result.explanation.romeo
          state.openalex = result.explanation.openalex

          if (!result.publication) {
            state.serverError = true
          } else {
            state.serverError = false
          }
        })
        .catch(error => {
          console.log(error)
          state.serverError = true
        })
        .finally(() => {
          state.searching = false
        })
    }

    if (autoClassify) {
      classify()
    }

    return {
      ...toRefs(state),
      v$,
      hasData,
      isError,
      doiError,
      classify,
      coatDescription,
      sourceField,
      sources2String
    }
  }
})

</script>

<style>
</style>
