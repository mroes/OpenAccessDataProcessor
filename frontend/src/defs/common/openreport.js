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

import { downloadFile } from 'src/defs/common/downloadfile'

export function openReport (name, args) {
  return new Promise((resolve, reject) => {
    const reportUrl = 'api/download/' + name
    downloadFile(reportUrl, undefined, args).then(result => {
      if (result.messages && result.messages.length > 0) {
        reject(result)
      } else {
        resolve(result)
      }
    })
      .catch(error => {
        let msg
        let messages
        if (error && error.length && error.charAt(0) === '{') {
        // JSON
          const jsonError = JSON.parse(error)
          if (jsonError.errorCode === 'report_error_messages' && jsonError.details && jsonError.details.length) {
            messages = jsonError.details
          }
          msg = jsonError.message
          if (!msg) {
            msg = jsonError.error
          }
        } else {
          msg = error
        }
        const errorResult = {
          messages: messages,
          errorMsg: msg
        }
        reject(errorResult)
      })
  })
}
