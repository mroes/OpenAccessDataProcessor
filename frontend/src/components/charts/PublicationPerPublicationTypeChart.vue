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
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { computed, defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { OAColor, PublicationColor, PublicationPublicationTypeStats } from 'components/models'
import { usePublicationStore } from 'stores/publication-store'
import { Series, TooltipParam } from 'components/charts/echarts'
import {
  compressColors,
  convertTo100Percent,
  displayTooltip,
  formatValue,
  sumPerPublisherAndColor
} from 'src/defs/chartutil'
import { OA_CLOSED, OA_UNKNOWN } from 'src/defs/const'
import { useCommonStore } from 'stores/common-store'

use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);

/*
 The OA color "closed" is displayed in its own stack by default.
 If there should be only one stack for all colors, the parameter `stackForClosed = false` can be passed.
 */
function buildSeries(oaColors : Array<OAColor>, stats : PublicationPublicationTypeStats, stackForClosed = true,
                     stackForUnknown = true, stackPercent = false) {
  const publishers: Array<string> = []  // PublicationType
  const series: Array<Series> = []
  const emphasis = {
    focus: 'series'
  }
  if (oaColors && stats) {

    const publisherKeys = new Map<string, number>()
    let index = 0
    stats.publicationTypes.forEach((pubtype) => {
      publishers.push(pubtype.name)
      publisherKeys.set(pubtype.id.toString(), index)
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

function sortByPublicationType(diagramData: { series: Array<Series>; publishers: Array<string> }) {
  let publicationTypes = diagramData.publishers;
  if (diagramData.series && publicationTypes) {
    // Zahlen pro Publikationsart berechnen
    let totals = new Array(publicationTypes.length).fill(0)
    diagramData.series.forEach(item => {
      for (let i = 0; i < publicationTypes.length; i++) {
        totals[i] += item.data[i]
      }
    })
    // Index-Array erzeugen
    let indizes = new Array(publicationTypes.length)
    for (let i = 0; i < publicationTypes.length; i++) {
      indizes[i] = i
    }
    // Index-Array nach Anzahl absteigend sortieren
    indizes.sort((a, b) => totals[b] - totals[a]);
    // console.log(indizes)
    // Daten entsprechend der Sortierung umstellen
    let sortedPublicationTypes:string[] = []
    indizes.forEach(idx =>  {
      sortedPublicationTypes.push(publicationTypes[idx])
    })
    diagramData.publishers = sortedPublicationTypes
    diagramData.series.forEach(item => {
      let sortedData:number[] = []
      indizes.forEach(idx =>  {
        sortedData.push(item.data[idx])
      })
      item.data = sortedData
    })
  }
}


export default defineComponent({
  name: 'PublicationPerPublicationTypeChart',
  components: {
    VChart
  },
  provide: {
//    THEME_KEY: 'light'
  },
  props: {
    values: {
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
      const data : PublicationPublicationTypeStats = {
        publicationTypes : [],
        numbersPerYearAndInstitution: new Map
      }
      let numbers = props.values
      if (numbers) {
        // Im feld publisherId steht die ID der Publikationsart
        numbers = sumPerPublisherAndColor(props.values)

        const publicationTypes = commonStore.getPublicationTypes
        publicationTypes.forEach(pubType => {
          const publicationType = commonStore.getPublicationType(pubType.id)
          data.publicationTypes.push(publicationType ? publicationType : {
            id: pubType.id,
            name: pubType.id.toString()
          })
          let numberPerPublisher = numbers.filter(pc2 => pc2.publisherId == pubType.id.toString())
          if (props.compress) {
            numberPerPublisher = compressColors(numberPerPublisher)
          }
          data.numbersPerYearAndInstitution.set(pubType.id.toString(), numberPerPublisher)
        })
        if (props.compress) {
          oaColors = commonStore.getOAColorsReduced
        }
      }
      const diagramData = buildSeries(oaColors, data, stackForClosed, stackForUnknown, props.stackPercent)
      // Sortieren nach der Anzahl der Publikationsarten
      sortByPublicationType(diagramData)
      // nach der Sortierung, da wir die absoluten Anzahlen für die Sortierung brauchen
      if (props.stackPercent) {
        convertTo100Percent(diagramData.series);
      }
      // console.log('yearchart')
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
        xAxis: [
          {
            type: 'category',
            data: diagramData.publishers,
            axisLabel: {
              interval: 0,
              fontSize: 14,
              // rotate: 20 //If the label names are too long you can manage this by rotating the label.
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
//            name: 'Anzahl',
            axisLabel: {
              fontSize: 14,
              formatter: '{value}'
            },
            // definieren von max, aber ohne Wert
            max: null as unknown as number
          }
        ],
        series: diagramData.series // [...series]
      }
      if (props.stackPercent) {
        const yAxis = option.yAxis[0]
        yAxis.axisLabel = {
          fontSize: 14,
          formatter: '{value} %'
        }
        yAxis.max = 100
      }
      return option
    })

    return {
      option,
      t,
      publicationStore
    };
  }
});
</script>

<style scoped>
</style>
