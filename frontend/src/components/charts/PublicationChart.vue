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
    <!-- Vue ECharts component -->
    <v-chart :option="option" class="echart" />
    <InfoButton text-id="index_piechart_text" />
  </div>
</template>

<script lang="ts">
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { LegendComponent, TitleComponent, TooltipComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { computed, defineComponent, PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import { DiagramData, MinMax, OAColor, PublicationColor } from 'components/models'
import { compressColors, sumPerOAColor, totalSum } from 'src/defs/chartutil'
import { useCommonStore } from 'stores/common-store'
import InfoButton from 'components/InfoButton.vue'

use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
])


function prepareLegend(oaColors : Array<OAColor>) {
  const legendNames: string[] = []
  const colors: string[] = []
  if (oaColors) {
    oaColors.forEach( oAcolor => {
      legendNames.push(oAcolor.name)
      colors.push(oAcolor.color)
    } )
  }
  return {
    names: legendNames,
    colors: colors
  }
}

function addValueForNotExistingColors(values: Array<PublicationColor>, oaColors: Array<OAColor>) {
  const result : Array<PublicationColor> = []
  if (values && oaColors) {
    oaColors.forEach( oAcolor => {
      const pc = values.find(pc => pc.name === oAcolor.name)
      if (pc) {
        result.push(pc)
      } else {
        result.push(
          {
            name: oAcolor.name,
            value: 0,
            year: 0,
            institutionId: '',
            publisherId: '',
            mainPublisherId: '',
            corresponding: true
          }
        )
      }
    } )
  }
  return result
}

export default defineComponent({
  name: 'PublicationChart',
  components: {
    InfoButton,
    VChart
  },
  props: {
    data: {
      type: Object as PropType<DiagramData>,
      required: true
    },
    yearRange: {
      type: Object as PropType<MinMax>,
      required: true
    },
    compressColors: {
      type: Boolean,
      required: false,
      default: true
    },
    showUnknown: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup (props, { emit }) {
    // eslint-disable-next-line @typescript-eslint/unbound-method
    const { t } = useI18n() // call `useI18n`, and spread `t` from  `useI18n` returning

    const commonStore = useCommonStore()

    const option = computed( () => {

      let oaColors = commonStore.getOAColors
      let values = addValueForNotExistingColors(props.data.totalValues, oaColors)
      if (props.compressColors) {
        oaColors = commonStore.getOAColorsReduced
        values = sumPerOAColor(compressColors(values))
      }

      let totalCount = totalSum(values)

      const legend = prepareLegend(oaColors)

      let yearText
      if (props.yearRange.min == props.yearRange.max) {
        yearText = props.yearRange.min
      } else {
        yearText = props.yearRange.min + '-' + props.yearRange.max
      }
      const titleText = totalCount.toString() + ' ' + t('publications')  + ' (' + yearText + ')'

      return {
      title: {
        text: titleText,
        left: 'center',
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        right: '5px',
        top: '20%',
        data: legend.names
      },
      series: [
        {
          name: t('publications'),
          type: 'pie',
          radius: '70%',
          center: ['40%', '60%'],
          data: values,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          color : legend.colors
        }
      ]
      /* ,
      graph: {
          color : colorPalette
        }
        */
    }})

    const showInfo = () => {
      emit('info-clicked')
    }

    return {
      option,
      showInfo
    }
  }
})
</script>

<style scoped>
.chart-container {
  position: relative;
  width: 600px;
  height: 400px;
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
