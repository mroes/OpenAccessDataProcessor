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
  <div>
    <v-chart class="" :option="option" />
  </div>
</template>

<script lang="ts">
// import type { EChartsOption } from 'echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart /*, { THEME_KEY } */ from 'vue-echarts'
import {defineComponent, computed} from 'vue'
import { useI18n } from 'vue-i18n'
import { PublicationColor, PublicationPublisherStats, Publisher } from 'components/models'
import {usePublicationStore} from 'stores/publication-store'
import {OAColor} from 'components/models'
import { Series, TooltipParam } from 'components/charts/echarts';
import {
  compressColors, convertTo100Percent, displayTooltip, formatValue, sumPerPublisher,
  sumPerPublisherAndColor
} from 'src/defs/chartutil';
import { OA_CLOSED, OA_UNKNOWN, UNKNOWN_PUBLISHER } from 'src/defs/const'
import { useCommonStore } from 'stores/common-store'

use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);

const MAX_PUBLISHERS = 10

function buildSeries(oaColors : Array<OAColor>, stats : PublicationPublisherStats, stackForClosed = true,
                     stackForUnknown = true, stackPercent = false) {
  const publishers: Array<string> = []
  const series: Array<Series> = []
  const emphasis = {
    focus: 'series'
  }
  if (oaColors && stats) {

    const publisherKeys = new Map<string, number>()
    let index = 0
    stats.publishers.forEach((publisher) => {
      publishers.push(publisher.name)
      publisherKeys.set(publisher.id, index)
      index++
    })

    if (publishers.length > 0) {
      oaColors.forEach( oAcolor => {
        const data : Array<number> = Array(publishers.length)
        data.fill(0, 0)
        stats.numbersPerYearAndInstitution.forEach((values, key) => {
          values.forEach( pc => {
            if (pc.name == oAcolor.name) {
              let index = publisherKeys.get(key)
              if (index != undefined) {
                data[index] += pc.value
              } else {
//                console.log(key)
              }
            }
          })
        })
        let stack
        if (stackForUnknown && oAcolor.name == OA_UNKNOWN) {
          stack = 'unknown'
        } else if (stackForClosed && oAcolor.name == OA_CLOSED) {
          stack = 'closed'
        } else {
          stack = 'oa'
        }
        const item = {
          name: oAcolor.name,
          type: 'bar',
          stack: stack,
          emphasis: emphasis,
          color: oAcolor.color,
          data: data,
          tooltip: {
            valueFormatter: (value: string) => formatValue(value, stackPercent)
          }
        }
        series.push(item)
      } )
    }
  }

  return {
    series,
    publishers: publishers
  }
}

export default defineComponent({
  name: 'PublicationPerPublisherChart',
  components: {
    VChart
  },
  provide: {
//    THEME_KEY: 'light'
  },
  props: {
    values: {
//      type: Object as PropType<PublicationPublisherStats>,
      type: Array<PublicationColor>,
      required: true
    },
    compress: {
      type: Boolean,
      required: false,
      default: false
    },
    stackForClosed: {
      type: Boolean,
      required: false,
      default: true
    },
    stackForUnknown: {
      type: Boolean,
      required: false,
      default: true
    },
    stackPercent: {
      type: Boolean,
      required: false,
      default: false
    },
    verticalBars: {
      type: Boolean,
      required: false,
      default: true
    }
  },
  setup (props) {

    // eslint-disable-next-line @typescript-eslint/unbound-method
    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const publicationStore = usePublicationStore()
    const commonStore = useCommonStore()

    const option = computed( () => {

      let oaColors = commonStore.getOAColors
      let stackForClosed = props.stackForClosed
      let stackForUnknown = props.stackForUnknown
      if (props.stackPercent) {
        stackForClosed = false
        stackForUnknown = false
      }
      const data : PublicationPublisherStats = {
        publishers : [],
        numbersPerYearAndInstitution: new Map
      }
      let numbers = props.values
      if (numbers) {
        numbers = sumPerPublisherAndColor(props.values)
        let publicationRecs = sumPerPublisher(numbers)
            .sort((pc1,pc2) => { return pc2.value - pc1.value })

        publicationRecs.every(pc => {
          if (pc.publisherId != UNKNOWN_PUBLISHER) {
            const publisher = commonStore.getPublisher(pc.publisherId)
            data.publishers.push(publisher ? publisher : {
              id: pc.publisherId,
              name: pc.publisherId
            } as Publisher)
            let numberPerPublisher = numbers.filter(pc2 => pc2.publisherId == pc.publisherId)
            if (props.compress) {
              numberPerPublisher = compressColors(numberPerPublisher)
            }
            data.numbersPerYearAndInstitution.set(pc.publisherId, numberPerPublisher)
            if (data.publishers.length >= MAX_PUBLISHERS) {
              return false
            }
          }
          return true
        })
        if (props.compress) {
          oaColors = commonStore.getOAColorsReduced
        }
      }
      const { series, publishers } = buildSeries(oaColors, data, stackForClosed, stackForUnknown, props.stackPercent)
      if (props.stackPercent) {
        convertTo100Percent(series);
      }

      let xAxis
      let yAxis
      if (props.verticalBars) {
        xAxis = {
          type: 'category',
          data: publishers,
//          name: '%OA Anteile',
          axisLabel: {
            interval: 0,
            rotate: 20, //If the label names are too long you can manage this by rotating the label.
//              color: 'red',
            fontSize: 13
          }
        }
        yAxis = {
          type: 'value',
//          title: 'Verlag',
          axisLabel: {
            fontSize: 14,
            formatter: '{value}'
          },
          max: null as unknown as number
        }
        if (props.stackPercent) {
          yAxis.axisLabel.formatter = '{value} %'
          yAxis.max = 100
        }
      } else {
        xAxis = {
          type: 'value',
          axisLabel: {
            interval: 0,
//            rotate: 20, //If the label names are too long you can manage this by rotating the label.
            fontSize: 13,
            formatter: '{value}'
          },
          max: null as unknown as number
        }
        yAxis = {
          type: 'category',
          data: publishers,
          inverse: true,
//            name: 'Anzahl',
          axisLabel: {
            fontSize: 14
          }
        }
        if (props.stackPercent) {
          xAxis.axisLabel.formatter = '{value} %'
          xAxis.max = 100
        }
      }

      const option /* : EChartsOption */ = {
        /*
        title: {
          text: t('publications_per_year'),
          left: 'center'
        },
         */
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: (params: Array<TooltipParam>) => {
            return displayTooltip(params, props.stackPercent, !props.stackPercent)
          }
        },
      /*
        legend: {
          orient: 'vertical',
          left: 'left',
          top: '20%',
          data: legend.names
        },
       */
        legend: {},
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [xAxis],
        yAxis: [yAxis],
        series: series
      }
      return option
    })

    return {
      option,
      t,
      publicationStore
    }
  }
})
</script>

<style scoped>
</style>
