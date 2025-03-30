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
  <!-- Info Button -->
  <q-btn
    v-if="textId"
    flat
    round
    icon="info"
    size="md"
    color="primary"
    class="info-button"
  >
    <q-tooltip class="tooltip bordered"
               :offset="[10, 10]"
               :hide-delay="0"
    >
      <div class="tooltipbox">
        <vue-markdown-it v-if="markdown" :source="$t(textId)"  />
        <span v-else>{{ $t(textId) }}</span>
      </div>
    </q-tooltip>
  </q-btn>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { VueMarkdownIt } from '@f3ve/vue-markdown-it'


export default defineComponent({
  name: 'InfoButton',
  components: {
    VueMarkdownIt
  },
  props: {
    textId: {
      type: String,
      required: true
    },
    markdown: {
      type: Boolean,
      default: false
    }
  },
  setup( /* props */ ) {
    return {

    }
  }
})
</script>

<style>
.tooltip {
  background-color: #ffffff;
  color: black; /* Dark text for readability */
  font-family: Arial, sans-serif;
  font-size: 14px;
  line-height: 1.5;
  padding: 10px 15px;
  border-radius: 5px;
}
</style>
