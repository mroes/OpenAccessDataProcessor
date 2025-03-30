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

// import SeriesModel from 'echarts/types/src/model/Series'

export interface Series {
  name: string,
  type: string,
  stack: string,
  emphasis: object,
  color: string,
  data: Array<number>
}

export interface TooltipParam {
  componentType: 'series',
    // Series type
    seriesType: string,
  // Series index in option.series
  seriesIndex: number,
  // Series name
  seriesName: string,
  // Data name, or category name
  name: string,
  // Data index in input data array
  dataIndex: number,
  // Original data as input
//  data: Object,
  // Value of data. In most series it is the same as data.
  // But in some series it is some part of the data (e.g., in map, radar)
  value: number /* |Array|Object */,
  // encoding info of coordinate system
  // Key: coord, like ('x' 'y' 'radius' 'angle')
  // value: Must be an array, not null/undefined. Contain dimension indices, like:
  // {
  //     x: [2] // values on dimension index 2 are mapped to x axis.
  //     y: [0] // values on dimension index 0 are mapped to y axis.
  // }
  /*
  encode: Object,
  // dimension names list
  dimensionNames: Array<String>,
  // data dimension index, for example 0 or 1 or 2 ...
  // Only work in `radar` series.
  dimensionIndex: number,

   */
  // Color of data
  color: string,
  /*
  // The percentage of current data item in the pie/funnel series
  percent: number,
  // The ancestors of current node in the sunburst series (including self)
  treePathInfo: Array,
  // The ancestors of current node in the tree/treemap series (including self)
  treeAncestors: Array

   */
  marker: string
}
