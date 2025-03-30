/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import { PublicationColor } from 'components/models'
import { Series, TooltipParam } from 'components/charts/echarts'

function createTooltipRow (name: string, value: string|number, color: string, marker: string|undefined = undefined) {
  if (!marker) {
    marker = `<div style="color:${color}">&#11044;</div>`
  }
  return `<div style="display:flex">
          ${marker}
          <div style="margin-left: 5px; margin-right: 10px">${name}</div>
          <div style="font-weight: bold; margin-left: auto;text-align: right;">`
          + value + '</div><br/></div>'

}

export function displayTooltip (params: Array<TooltipParam>, percent = false, showTotal = true) : string {
  let tooltip = `${params[0].name} <br />`
  let total = 0
  for (let i=0; i < params.length; i++) {
    const value = params[i].value
    total += value
    /*
    tooltip += `<div style="display:flex">
          <div style="color:${params[i].color}">&#11044;</div>
          <div style="margin-left: 5px; margin-right: 10px">${params[i].seriesName}</div>
          <div style="font-weight: bold; margin-left: auto;text-align: right;">`
      + formatValue(value, percent) + '</div><br/></div>'
     */
    let colorName = params[i].color
    if (!colorName) {
      colorName = params[0].color
    }
    tooltip += createTooltipRow(params[i].seriesName, formatValue(value, percent), colorName)
  }
  if (showTotal) {
    tooltip += '<hr/>'
    tooltip += createTooltipRow('Gesamt', formatValue(total, percent), 'black')
  }
  return tooltip
}

export function compressColors(values : Array<PublicationColor>) {
  const result: Array<PublicationColor> = []
  if (values) {
    values.forEach(pc => {
      const pcn = Object.assign({}, pc)
      switch (pc.name) {
        case 'Diamant':
        case 'Gold-pur':
          pcn.name = 'Gold'
          break
        case 'Hybrid':
          pcn.name = 'Hybrid'
          break
        case 'Green-Post':
        case 'Green-Pre':
          pcn.name = 'Green'
          break
        case 'Unknown':
          pcn.name = 'Unknown'
          break
        default:
          pcn.name = 'Closed'
      }
      result.push(pcn)
    })
  }
  return result
}

export function compressColorsInMap(valuesPerPublisher: Map<string, Array<PublicationColor>>) {
  if (valuesPerPublisher) {
    const result = new Map<string, Array<PublicationColor>>
    valuesPerPublisher.forEach((values, publisher) => {
      const valuesNew = compressColors(values)
      result.set(publisher, valuesNew)
    })
    return result
  }
  return valuesPerPublisher
}

function sumValues(values: Array<PublicationColor>, calcKey: (pc:PublicationColor) => string) {
  const result: Array<PublicationColor> = []
  if (values) {
    const sumMap = new Map<string, PublicationColor>
    values.forEach(pc => {
      const key = calcKey(pc)
      let entry = sumMap.get(key)
      if (!entry) {
        entry = Object.assign({}, pc)
        sumMap.set(key, entry)
      } else {
        entry.value += pc.value
      }
    })
    return Array.from(sumMap.values())
  }
  return result
}

export function sumPerOAColor(values: Array<PublicationColor>) {
  return sumValues(values, (pc) => pc.name + '')
}

export function sumPerPublisherAndColor(values: Array<PublicationColor>) {
  return sumValues(values, (pc) => pc.publisherId + ':' + pc.name)
}

export function sumPerPublisherAndColorAndYear(values: Array<PublicationColor>) {
  return sumValues(values, (pc) => pc.publisherId + ':' + pc.name + ':' + pc.year)
}

export function sumPerPublisherAndInstitutionAndYear(values: Array<PublicationColor>) {
  return sumValues(values, (pc) => pc.publisherId + ':' + pc.institutionId + ':' + pc.year)
}

export function sumPerPublisher(values: Array<PublicationColor>) {
  return sumValues(values, (pc) => pc.publisherId + '')
}

export function totalSum(values : Array<PublicationColor>) {
  let count = 0
  if (values) {
    values.forEach( pc => {
      count += pc.value
    })
  }

  return count
}


/*
 Converts the data into percentages for representation as a "Percentage Stacked Bar Chart".
 One series per color
 series.data array contains the values per column
*/
export function convertTo100Percent(series: Array<Series>) {
  if (series && series.length > 0) {
    // Determine the total per column.
    // We take the number of columns from the length of `data` in the first series.
    const columnsCount = series[0].data.length
    const totalByColumn = Array(columnsCount)
    totalByColumn.fill(0, 0)
    for (let i = 0; i < columnsCount; i++) {
      let total = 0
      series.forEach(serie => {
        if (serie.data && serie.data.length > i) {
          total = total + serie.data[i]
        }
      })
      totalByColumn[i] = total;
    }
    // normalize
    series.forEach( serie => {
      if (serie.data && serie.data.length > 0) {
        for (let i = 0;  i < serie.data.length; i++) {
          serie.data[i] = serie.data[i] / totalByColumn[i] * 100
        }
      }
    })
  }
  return series;
}

export function formatValue(value: number|string, stackPercent: boolean) {
  if (stackPercent) {
    if (typeof value == 'string') {
      value = parseFloat(value)
    }
    const val = value.toFixed(1)
    return val + ' %'
  } else {
    return value.toLocaleString('de')
  }
}
