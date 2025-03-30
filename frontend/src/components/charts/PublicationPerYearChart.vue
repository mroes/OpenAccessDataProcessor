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
  <div class="chart-container">
    <v-chart class="echart" :option="option" />
    <InfoButton :text-id=infoTextKey />
  </div>
</template>

<script lang="ts">
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { computed, defineComponent, PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { Institution, MinMax, OAColor, PublicationColor } from 'components/models'
import { Series, TooltipParam } from 'components/charts/echarts'
import {
  compressColors,
  convertTo100Percent,
  displayTooltip,
  formatValue
} from 'src/defs/chartutil'
import { useCommonStore } from 'stores/common-store'
import { OA_CLOSED, OA_UNKNOWN } from 'src/defs/const'
import InfoButton from 'components/InfoButton.vue'

use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

function buildSeries(oaColors : Array<OAColor>, numbers : Array<PublicationColor>, yearRange: MinMax,
                     stackForClosed = true, stackForUnknown = true, stackPercent = false) {
  const years: Array<number> = []
  const series: Array<Series> = []
  const emphasis = {
    focus: 'series'
  }
  if (oaColors && numbers) {
    for (let year = yearRange.min; year <= yearRange.max; year++) {
      years.push(year)
    }

    let yearsCount = yearRange.max - yearRange.min + 1
    if (yearsCount > 0) {
      oaColors.forEach( oAcolor => {
        const data : Array<number> = Array(yearsCount)
        data.fill(0, 0)
        numbers.forEach(pc => {
          if (pc.name == oAcolor.name) {
            data[pc.year - yearRange.min] += pc.value
          }
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
    years: years
  }
}

// colours for institutions
const colors = ['#FFC312',
  '#C4E538',
  '#12CBC4',
//  '#FDA7DF',
//  '#ED4C67',
  '#F79F1F',
  '#A3CB38',
  '#1289A7',
  //'#D980FA',
  // '#B53471',
  '#EE5A24',
  '#009432',
  '#0652DD',
  // '#9980FA',
  //'#833471',
  '#EA2027',
  '#006266',
  '#1B1464'
]

function buildSeriesByInstitution(institutions : Array<Institution>, numbers : Array<PublicationColor>, yearRange: MinMax,
                                  stackPercent = false, includeTotal = false) {
  const years: Array<number> = []
  const series: Array<Series> = []
  const emphasis = {
    focus: 'series'
  }
  if (institutions && numbers) {
    for (let year = yearRange.min ; year <= yearRange.max; year++) {
      years.push(year)
    }
    if (includeTotal) {
      years.push(9999)
    }

    let yearsCount = yearRange.max - yearRange.min + 1
    if (yearsCount > 0) {
      let idx = 0
      institutions.forEach( inst => {
        let dataSize = yearsCount
        if (includeTotal) {
          dataSize++;
        }
        const data : Array<number> = Array(dataSize)
        data.fill(0, 0)
        numbers.forEach(pc => {
          if (pc.institutionId == inst.id) {
            data[pc.year - yearRange.min] += pc.value
            if (includeTotal) {
              data[yearsCount] += pc.value
            }
          }
        })
        let stack = 'default'
        let color = idx < colors.length ? colors[idx] : ''
        const item = {
          name: inst.sname,
          type: 'bar',
          stack: stack,
          emphasis: emphasis,
          color: color,
          data: data,
          tooltip: {
            valueFormatter: (value: string) => formatValue(value, stackPercent)
          }
        }
        series.push(item)
        idx++
      } )
    }
  }

  return {
    series,
    years: years
  }
}



export default defineComponent({
  name: 'PublicationPerYearChart',
  components: {
    InfoButton,
    VChart
  },
  props: {
    values: {
      type: Array<PublicationColor>,
      required: true
    },
    yearRange: {
      type: Object as PropType<MinMax>,
      required: true
    },
    byColor: {
      type: Boolean,  // Bars divided by colors or Bars divided by institutions
      required: false,
      default: true
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
    infoTextKey: {
      type: String,
      required: false,
      default: ''
    },

  },
  setup (props, {emit}) {

    // eslint-disable-next-line @typescript-eslint/unbound-method
    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const commonStore = useCommonStore()

    const option = computed( () => {
      let oaColors = commonStore.getOAColors
      let values = props.values
      let data
      if (props.byColor) {
        let stackForClosed = props.stackForClosed
        let stackForUnknown = props.stackForUnknown
        if (props.stackPercent) {
          stackForClosed = false
          stackForUnknown = false
        }
        if (props.compress) {
          oaColors = commonStore.getOAColorsReduced
          values = compressColors(props.values)
        }
        data = buildSeries(oaColors, values, props.yearRange, stackForClosed, stackForUnknown, props.stackPercent)
      } else {
        const institutions = commonStore.getInstitutions
        data = buildSeriesByInstitution(institutions, values, props.yearRange, props.stackPercent)
      }
      if (props.stackPercent) {
        convertTo100Percent(data.series);
      }
      const option /* : EChartsOption */ = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: (params: Array<TooltipParam>) => {
            return displayTooltip(params, props.stackPercent, !props.stackPercent)
          },
        },
        legend: {
          itemGap: props.byColor ? 10  : 5
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '20%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: data.years,
            axisLabel: {
              interval: 0,
//              rotate: 30 //If the label names are too long you can manage this by rotating the label.
              fontSize: 14
            }
          }
        ],
        yAxis: [
          {
            type: 'value',
            axisLabel: {
              formatter: '{value}',
              fontSize: 14
            },
            max: null as unknown as number
          }
        ],
        series: data.series
      }
      if (props.stackPercent) {
        const yAxis = option.yAxis[0]
        yAxis.axisLabel = {
          formatter: '{value} %',
          fontSize: 14
        }
        yAxis.max = 100
      }
      return option
    })

    const showInfo = () => {
      emit('info-clicked')
    }

    return {
      option,
      t,
      publicationStore: commonStore,
      infoText: props.infoTextKey ? t(props.infoTextKey) : '',
      showInfo
    }
  }
})
</script>

<style scoped>
.chart-container {
  position: relative;
  width: 1000px;
  height: 600px;
}

.echart {
  width: 100%;
  height: 100%;
}

/* css for positioning the info button */
.info-button {
  position: absolute;
  top: -10px;
  left: 5px;
}
</style>
