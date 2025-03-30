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
    <div style="font-weight: bold">
    {{ publication.title }}
    </div>
<p></p>
<div class="row" v-if="publication.id">
  <div class="column" style="max-width: 350px">
    <q-list>
      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('id') }}</q-item-label>
          <q-item-label>{{ publication.id }}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('doi') }}</q-item-label>
          <q-item-label v-if="publication.doi"><a :href="'https://doi.org/' + publication.doi" target="_new">{{ publication.doi }}</a></q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('publication_year') }}</q-item-label>
          <q-item-label>{{ publication.year }}</q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('publication_type') }}</q-item-label>
          <q-item-label>{{ publication.type ? publication.type.name : '' }}
          </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('authors') }}</q-item-label>
          <q-item-label>
            <q-scroll-area style="height: 100px; max-width: 300px;" :visible="true">
              <div v-for="(author,idx) in publication.authors" :key="idx">
                <span v-if="author.corr">* </span>{{ author.firstName }} {{ author.lastName }}
              </div>
            </q-scroll-area>
          </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('institution') }}</q-item-label>
          <q-item-label>
            <div v-for="(inst,idx) in institutions" :key="idx">
              <span v-if="inst.corr">* </span>{{ labelForInstitution(inst.institutionId) }}
            </div>
          </q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('publisher') }}</q-item-label>
          <q-item-label>{{ publication.publisher.name }}</q-item-label>
          <q-item-label v-if="publication.publisher.wikidataId">
            {{ $t('wikidata') }} <a :href="'https://www.wikidata.org/wiki/' + publication.publisher.wikidataId" target="_new">{{ publication.publisher.wikidataId }}</a>
          </q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('mainpublisher') }}</q-item-label>
          <q-item-label>{{ publication.mainPublisher.name }}</q-item-label>
          <q-item-label v-if="publication.mainPublisher.wikidataId">
            {{ $t('wikidata') }} <a :href="'https://www.wikidata.org/wiki/' + publication.mainPublisher.wikidataId" target="_new">{{ publication.mainPublisher.wikidataId }}</a>
          </q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('journal') }}</q-item-label>
          <q-item-label>{{ publication.journal.title }}</q-item-label>
          <q-item-label v-if="publication.journal.wikidataId">
            {{ $t('wikidata') }} <a :href="'https://www.wikidata.org/wiki/' + publication.journal.wikidataId" target="_new">{{ publication.journal.wikidataId }}</a>
          </q-item-label>
          <q-item-label>{{ $t('issn') }}: {{ arrayToString(publication.journal.issn) }}</q-item-label>
        </q-item-section>
      </q-item>

    </q-list>
  </div>
  <div class="column" style="width: 0; margin-left: 100px">
  </div>
  <div class="column" style="max-width: 350px">
    <q-list>
      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('coat') }}</q-item-label>
          <q-item-label>{{ publication.coat }}
            <span v-if="publication.doi">
              <q-btn round color="blue" size="xs" icon="fas fa-glasses" :href="'/coatclassify?doi=' + publication.doi" target="_new" />
            </span>
          </q-item-label>
          <q-item-label>{{ publication.color }}
          </q-item-label>
        </q-item-section>
      </q-item>
      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('colorUpw') }}</q-item-label>
          <q-item-label>{{ publication.colorUpw }}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('licenceUrl') }}</q-item-label>
          <q-item-label>{{ publication.licence.url }}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('version') }}</q-item-label>
          <q-item-label>{{ publication.version }}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('oaPlace') }}</q-item-label>
          <q-item-label>{{ publication.oaPlace }}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('oaUrl') }}</q-item-label>
          <q-item-label style="max-width: 350px; text-overflow: ellipsis;overflow: hidden;white-space: nowrap;"
                        v-if="publication.oaVersionLink"><a :href="publication.oaVersionLink" target="_new">{{ publication.oaVersionLink }}</a></q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('embargoTime') }}</q-item-label>
          <q-item-label>{{ publication.embargoTime ? publication.embargoTime + ' ' + $t('months') : ''}}</q-item-label>
        </q-item-section>
      </q-item>

      <q-item>
        <q-item-section>
          <q-item-label overline>{{ $t('costs') }}</q-item-label>
          <q-item-label>{{ publication.costs.amount }} {{ publication.costs.currency }}</q-item-label>
        </q-item-section>
      </q-item>

    </q-list>
  </div>
</div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue'
import { Publication, SourceReference } from 'components/models'
import { arrayToString } from '../defs/common'
import { useCommonStore } from 'stores/common-store'

export default defineComponent({
  name: 'PublicationView',
  methods: { arrayToString },
  components: {
  },
  props: {
    // publication: expects an object with the structure of a publication
    // otherwise there will be exceptions when accessing e.g. publication.type.name
    publication: {
      type: Object as () => Publication,
      required: true
    }
  },
  setup(props) {

    const commonStore = useCommonStore()

    const institutions = computed (() => {
      const institutions: SourceReference[] = []
      if (props.publication) {
        // build array of distinct institution ids (sometimes a publication is delivered multiple times)
        const seen = new Set<string>();
        props.publication.sources?.forEach(source => {
          if (!seen.has(source.institutionId)) {
            seen.add(source.institutionId);
            institutions.push(source);
          }
        })
      }
      return institutions
    })

    return {
      institutions,
      labelForInstitution : commonStore.labelForInstitution,
      labelForPublicationType : commonStore.labelForPublicationType
    }
  }
})
</script>


<style>
</style>

