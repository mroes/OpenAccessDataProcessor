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
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { computed, defineComponent, PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { MinMax, OAColor, PublicationColor } from 'components/models'
import { Series, TooltipParam } from 'components/charts/echarts'
import {
  compressColors,
  convertTo100Percent,
  displayTooltip,
  formatValue
} from 'src/defs/chartutil'
import { useCommonStore } from 'stores/common-store'

use([
  CanvasRenderer,
  BarChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
]);


function buildSeries(oaColors : Array<OAColor>, numbers : Array<PublicationColor>, licences: Array<string>,
                     licenceColors: Array<string>,
                     yearRange: MinMax, stackBars = false, stackPercent = false, lineChart = false) {
  const years: Array<string> = []
  const series: Array<Series> = []
  const emphasis = {
    focus: 'series'
  }
  if (oaColors && licences && numbers) {
    let chartType : string
    if (lineChart) {
      chartType = 'line'
    } else {
      chartType = 'bar'
    }
    let yearsCount = yearRange.max - yearRange.min + 1

    for (let year = yearRange.min; year <= yearRange.max; year++) {
      years.push(year.toString())
    }

    if (yearsCount > 0) {
      let idx = 0
      licences.forEach( licence => {
        const data : Array<number> = Array(yearsCount)
        data.fill(0)
        numbers.forEach(pc => {
          if (pc.publisherId == licence) {
            const yearIdx = pc.year - yearRange.min
            data[yearIdx] += pc.value
          }
        })
        let stack = licence
        if (stackBars) {
          stack = 'all'
        }
        /*
        if (stackForUnknown && oAcolor.name == 'Unknown') {
          stack = 'unknown'
        } else if (stackForClosed && oAcolor.name == 'Closed') {
          stack = 'closed'
        } else {
          stack = 'oa'
        }

         */
        let color = idx < licenceColors.length ? licenceColors[idx] : ''
        const item = {
          name: licence,
          type: chartType,
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



function intervalFunc(index : number, value: number) {
  return value ? true : false;
}


export default defineComponent({
  name: 'PublicationPerLicenceYearChart',
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
    yearRange: {
      type: Object as PropType<MinMax>,
      required: true
    },
    byLicence: {
      type: Boolean,  // Bars in Farben unterteilt oder Bars in Institutionen unterteilt
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
    verticalBars: {
      type: Boolean,
      required: false,
      default: true
    },
    lineChart: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup (props) {

    // eslint-disable-next-line @typescript-eslint/unbound-method
    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const commonStore = useCommonStore()

    const option = computed( () => {
      let oaColors = commonStore.getOAColors
      const licences = commonStore.getLicences
      const licenceColors = commonStore.getLicenceColors
      let values = props.values
      let data = {
        series: [] as Series[],
        years: [] as string[]
      }
      if (props.byLicence) {
        let stackLicences = !props.lineChart
        if (props.compress) {
          oaColors = commonStore.getOAColorsReduced
          values = compressColors(props.values)
        }
        data = buildSeries(oaColors, values, licences, licenceColors, props.yearRange, stackLicences, props.stackPercent, props.lineChart)
      }
      if (props.stackPercent) {
        convertTo100Percent(data.series);
      }

      let xAxes
      let yAxes
      if (props.verticalBars) {
        xAxes = [
          {
            type: 'category',
//            name: 'Jahre',
            data: data.years,
            position: 'bottom',
            axisLabel: {
              interval: 0,
//              margin: 40,
              align: 'left',
              fontSize: 14,
            },
            axisTick: {
              alignWithLabel: false,
//              length: 50,
              align: 'left',
//              interval: intervalFunc
            },
            splitLine: {
              show: true,
              interval: intervalFunc
            }
          }
        ]
        yAxes = [
          {
            type: 'value',
//            name: 'Anzahl',
            axisLabel: {
              fontSize: 14,
              formatter: '{value}'
            },
            max: null as unknown as number
          }
        ]
        if (props.stackPercent) {
          const yAxis = yAxes[0]
          yAxis.axisLabel.formatter = '{value} %'
          yAxis.max = 100
        }

      } else {
        // horizontale Balken
        xAxes = [{
          type: 'value',
          axisLabel: {
            fontSize: 14,
            formatter: '{value}',
//            margin: 40,
//            offset: 10
          },
          max: null as unknown as number
        }
        ]
        yAxes = [
          {
            type: 'category',
            data: data.years,
            position: 'left',
            axisLabel: {
              interval: 0,
              margin: 40,
              align: 'left',
              fontSize: 14,
            },
            axisTick: {
              alignWithLabel: false,
//              length: 50,
              align: 'left',
              interval: intervalFunc
            },
            splitLine: {
              show: true,
              interval: intervalFunc
            }
          }
        ]
        if (props.stackPercent) {
          const xAxis = xAxes[0]
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
          left: '20',
          right: '20',
          bottom: props.verticalBars ? '15' : '15',
          containLabel: true
        },
        xAxis: xAxes,
        yAxis: yAxes,
        series: data.series
      }

      return option

    })

    return {
      option,
      t
    };
  }
});
</script>

<style scoped>
.chart {
  height: 400px;
  width: 1000px;
/*  border: red 1px solid; */
}
</style>
