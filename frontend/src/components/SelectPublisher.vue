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
  <q-select v-model="data.selectedPublisher"
            outlined
            :options="publisherOptions"
            :label="$t('publisher')"
            option-value="id"
            option-label="name"
            emit-value
            map-options
            @update:model-value="onPublisherSelected"
            use-input
            input-debounce="0"
            hide-selected
            fill-input
            clearable
            @filter="publisherFilterFn"
  >
    <template v-slot:option="scope">
      <q-item v-bind="scope.itemProps" v-if="scope.opt?.name !== ''">
        <q-item-section>
          <q-item-label>
            <span :class="{ specialOption: scope.opt?.id == 'all'} ">
              {{ scope.opt?.name }}
            </span>
          </q-item-label>
        </q-item-section>
        <q-item-section side v-if="scope.focused">
              <span v-if="true">
                <a :href="'https://www.wikidata.org/wiki/' + scope.opt.wikiId" target="_blank">{{ scope.opt.wikiId }}</a>
              </span>
        </q-item-section>
      </q-item>
      <q-separator v-else inset color="black"/>
    </template>
  </q-select>

</template>

<script lang="ts">
import { defineComponent, onMounted, PropType, ref } from 'vue'
import { Publisher } from './models'
import { ALL_PUBLISHERS } from 'src/defs/const'
import { useI18n } from 'vue-i18n'

type DoneFn = (callbackFn:  () => void ) => void;

export default defineComponent({
  name: 'SelectPublisher',
  props: {
    modelValue: {
      type: String,
      default: ALL_PUBLISHERS,
    },
    publishers: {
      type: Array as PropType<Publisher[]>,
      default: () => [],
      required: true
    }
  },
  emits: ['update:model-value'],
  setup(props, context) {

    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const allPublishers = {
      id: ALL_PUBLISHERS,
      name: t('allpublishers')
    } as Publisher

    const data = ref({
      selectedPublisher: props.modelValue,
    })
    const publisherOptions = ref<Publisher[]>()


    const publisherFilterFn = (val: string, update: DoneFn /*, abort */) => {
      update(() => {
        if (props.publishers) {
          const publishers = [allPublishers].concat(props.publishers)
          const needle = val.toLowerCase()
          if (needle) {
            publisherOptions.value = publishers.filter(v => v ? v.name.toLowerCase().indexOf(needle) > -1 : false)
          } else {
            publisherOptions.value = publishers
          }
        } else {
          publisherOptions.value = [allPublishers]
        }
      })
    }

    const onPublisherSelected = (newValue: string) => {
      context.emit('update:model-value', newValue)
    }

    onMounted(() => {
      publisherOptions.value = [allPublishers].concat(props.publishers)
    })

    return {
      data,
      publisherOptions,
      publisherFilterFn,
      onPublisherSelected
    }
  }
})
</script>

<style>
.specialOption {
  font-weight: bold;
  font-style: italic;
}
</style>
