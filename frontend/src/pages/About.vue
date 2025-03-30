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
    <div class="text-h6">{{ $t('title_about') }}</div>
    <p></p>
    <p><vue-markdown-it :source="$t('txt_about')" :plugins="plugins" /></p>
    <p>{{ $t('txt_asOfDate') }}</p>
    <p>
    <q-list dense>
      <q-item v-for="(inst, index) in institutions" :key="index" dense>
        <q-item-section side><span class="numbering">{{ index+1 }}.</span></q-item-section>
        <q-item-section>{{ instLabel(inst) }}</q-item-section>
      </q-item>
    </q-list>
    </p>
    <p>
      {{ $t('labeL_pending') }}:
    </p>
    <p>
      <q-list dense>
        <q-item v-for="(inst, index) in institutionsPending" :key="index" dense>
          <q-item-section side><span class="numbering">{{ index+1 }}.</span></q-item-section>
          <q-item-section>{{ instLabel(inst) }}</q-item-section>
        </q-item>
      </q-list>
    </p>
  </q-page>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue'
import { useI18n } from 'vue-i18n'
import { buildInstitutionOptions } from 'src/defs/options_support'
import { Institution } from 'components/models'
import { useCommonStore } from 'stores/common-store'
import { VueMarkdownIt } from '@f3ve/vue-markdown-it'
import markdownItSup from 'markdown-it-sup'
import markdownItLinkAttributes from 'markdown-it-link-attributes'

export default defineComponent({
  name: 'AboutPage',
  components: {
    VueMarkdownIt
  },
  setup() {

    const { t, locale } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const isGerman = () => {
      if (locale && locale.value.startsWith('de')) {
        return true;
      } else {
        return false;
      }
    };

    const state = reactive({
      german: isGerman()
    })

    const plugins = [markdownItSup,
      [markdownItLinkAttributes, {
        attrs: {
          target: '_blank',
          rel: 'noopener',
        }
      }]
    ]

    const commonStore = useCommonStore()
    const institutionsPending = commonStore.getInactiveInstitutions
    if (state.german) {
      institutionsPending.sort((a, b) => a.name.localeCompare(b.name));
    } else {
      institutionsPending.sort((a, b) => a.nameEn.localeCompare(b.nameEn));
    }

    const instLabel = (inst : Institution) => {
      if (inst) {
        if (state.german || !inst.nameEn) {
          return inst.name
        } else {
          return inst.nameEn
        }
      }
      return ''
    }

    return {
      ...toRefs(state),
      plugins,
      instLabel,
      institutions : buildInstitutionOptions(t, false),
      institutionsPending
    }
  }
})
</script>

<style scoped>
.numbering {
  width: 15px;
  text-align: right;
}
</style>
