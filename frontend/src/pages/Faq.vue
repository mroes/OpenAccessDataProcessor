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
    <div class="text-h6">{{ $t('title_faq') }}</div>
    <p></p>
        <div class="">
        <q-list padding bordered>
          <q-expansion-item
            v-for="(faq, index) in faqs"
            :key="index"
            :label="faqLabel(faq)"
            v-model="expanded[index]"
            ref="faqItems"
            :header-class="{ 'expanded-header': expanded[index] }"
            group="mygroup"
          >
            <q-card>
              <q-card-section>
                <vue-markdown-it :source="faq.answer" :plugins="plugins" :anchorAttributes="{ target: '_blank', rel: 'noopener' }" />
              </q-card-section>
            </q-card>
          </q-expansion-item>
        </q-list>
        </div>
  </q-page>
</template>

<script lang="ts">
import { defineComponent, nextTick, reactive, ref, toRefs } from 'vue'
import { fetchFaqs } from 'src/defs/api'
import { Faq } from 'components/models'
import { useI18n } from 'vue-i18n'
import { VueMarkdownIt } from '@f3ve/vue-markdown-it';
import markdownItSup from 'markdown-it-sup'
import markdownItLinkAttributes from 'markdown-it-link-attributes'
import { useRoute } from 'vue-router'
import { QExpansionItem } from 'quasar'

export default defineComponent({
  name: 'FaqPage',
  components: {
    VueMarkdownIt
  },
  setup() {

    const state = reactive({
      faqs: [] as Array<Faq>
    })

    const faqItems = ref<Array<InstanceType<typeof QExpansionItem> | null>>([])
    const expandedFaqId = ref<number>(0)
    const expanded = ref<boolean[]>([])

    const plugins = [markdownItSup,
      [markdownItLinkAttributes, {
        attrs: {
          target: '_blank',
          rel: 'noopener',
        }
      }]
    ]

    const { locale } = useI18n()

    const route = useRoute()
    const faqId = route.params.id
    if (faqId && !Array.isArray(faqId)) {
      expandedFaqId.value = parseInt(faqId)
    }

    fetchFaqs(locale.value).then(faqs => {
      state.faqs = faqs
      expanded.value = new Array(faqs.length).fill(false)
      if (expandedFaqId.value > 0) {
        setExpanded(expandedFaqId.value)
      }
    })

    const faqLabel= (faq : Faq) => {
      if (faq) {
        return faq.number + '. ' + faq.question
      }
      return ''
    }

    // Function to set the expanded FAQ
    const setExpanded = (faqNumber: number) => {
      expanded.value.fill(false)
      for (let i = 0; i < state.faqs.length; i++) {
        if (state.faqs[i].number == faqNumber) {
          expanded.value[i] = true

          nextTick(() => {
            const item = faqItems.value[i]?.$el
            if (item instanceof HTMLElement) {
              item.scrollIntoView({ behavior: 'smooth', block: 'start' })
            }
          })

          break
        }
      }
    }

    return {
      ...toRefs(state),
      plugins,
      faqLabel,
      expanded,
      faqItems
    }
  }
})
</script>

<style>
.expanded-header {
  font-weight: bold;
}
</style>
