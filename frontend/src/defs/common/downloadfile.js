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

import axios from 'axios'
import { saveAs } from 'file-saver'
import { getBoundary, parseMultiPartBinary } from './multipartparser'

export function downloadFile (url, saveName, args = {}) {
  return new Promise((resolve, reject) => {
    axios({
      url: url,
      method: 'POST',
      responseType: 'arraybuffer',
      data: args
    }).then((response) => {
      const result = {}
      const contentType = response.headers['content-type']
      if (contentType) {
        const boundary = getBoundary(contentType)
        const array = new Int8Array(response.data)
        const fields = parseMultiPartBinary(array, boundary)
        // result = {}
        if (fields) {
          fields.forEach(f => {
            if (f.name === 'file' && f.data && f.data.length > 0) {
              result.filename = f.filename
              const report = new Blob([f.data])
              saveAs(report, f.filename)
            } else if (f.name === 'msg') {
              result.messages = f.data
            }
          })
        }
      } else {
        const report = new Blob([response.data])
        if (!saveName) {
          saveName = filenameFromHeader(response.headers)
          if (!saveName) {
            saveName = 'unknown'
          }
        }
        saveAs(report, saveName)
      }
      resolve(result)
    })
      .catch(error => {
        if (error.response && error.response.data) {
          const response = new Blob([error.response.data])
          response.text().then(text => reject(text))
        }
      })
  })
}

export function saveFileAs (data, saveName) {
  const blob = new Blob([data], { type: 'application/zip' })
  /*
  blob.text().then(t => {
    console.log(' blob = ' + t)
  })
   */
  saveAs(blob, saveName)
}

export function filenameFromHeader (headers) {
  const contentDisposition = headers['content-disposition']
  let fileName
  if (contentDisposition) {
    const fileNameMatch = contentDisposition.match(/filename="(.+)"/)
    if (fileNameMatch.length === 2) {
      fileName = fileNameMatch[1]
    }
  }
  return fileName
}

