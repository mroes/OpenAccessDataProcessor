/*
 adopted from https://github.com/freesoftwarefactory/parse-multipart/blob/master/multipart.js
 */
/**
 Multipart Parser (Finite State Machine)

 usage:

 let multipart = require('./multipart.js')
 let body = multipart.DemoData()  // raw body
 let body = new Buffer(event['body-json'].toString(),'base64') // AWS case

 let boundary = multipart.getBoundary(event.params.header['content-type'])
 let parts = multipart.Parse(body,boundary)

 // each part is:
 // { filename: 'A.txt', type: 'text/plain', data: <Buffer 41 41 41 41 42 42 42 42> }

 author:  Cristian Salazar (christiansalazarh@gmail.com) www.chileshift.cl
 Twitter: @AmazonAwsChile
 */

import { Buffer } from 'buffer'

export function parseMultiPartBinary (multipartBodyBuffer, boundary) {
  const process = function (part) {
    // will transform this object:
    // { header: 'Content-Disposition: form-data; name="uploads[]"; filename="A.txt"',
    //  info: 'Content-Type: text/plain',
    //  part: 'AAAABBBB' }
    // into this one:
    // { filename: 'A.txt', type: 'text/plain', data: <Buffer 41 41 41 41 42 42 42 42> }

    const parsedHeader = parseHeader(part.header)
    const file = parsedHeader.value
    //    let header = part.header.split(';')
    //    let file = obj(header[1]) // statt 2
    const contentType = part.info.split(':')[1].trim()
    file.type = contentType
    let data
    if (isText(contentType)) {
      data = Buffer.from(part.part)
    } else if (isJson(contentType)) {
      data = JSON.parse(Buffer.from(part.part))
    } else {
      data = Buffer.from(part.part) // funktioniert nicht: new Blob(part.part, {type: contentType})
    }
    file.data = data
    /*
    Object.defineProperty(file, 'type',
      { value: contentType, writable: true, enumerable: true, configurable: true })
    Object.defineProperty(file, 'data',
      { value: Buffer.from(part.part), writable: true, enumerable: true, configurable: true })

     */
    return file
  }
  //  let prev = null
  let lastline = ''
  let header = ''
  let info = ''
  let state = 0
  let buffer = []
  const allParts = []

  for (let i = 0; i < multipartBodyBuffer.length; i++) {
    const oneByte = multipartBodyBuffer[i]
    const prevByte = i > 0 ? multipartBodyBuffer[i - 1] : null
    const newLineDetected = !!((oneByte === 0x0a) && (prevByte === 0x0d))
    const newLineChar = !!((oneByte === 0x0a) || (oneByte === 0x0d))

    if (!newLineChar) { lastline += String.fromCharCode(oneByte) }

    if ((state === 0) && newLineDetected) {
      if (('--' + boundary) === lastline) {
        state = 1
      }
      lastline = ''
    } else if ((state === 1) && newLineDetected) {
      header = lastline
      state = 2
      lastline = ''
    } else if ((state === 2) && newLineDetected) {
      info = lastline
      state = 3
      lastline = ''
    } else if ((state === 3) && newLineDetected) {
      state = 4
      buffer = []
      lastline = ''
    } else if (state === 4) {
      if (lastline.length > (boundary.length + 4)) {
        lastline = ''
      }
      if (((('--' + boundary) === lastline))) {
        const j = buffer.length - lastline.length
        // The buffer still contains CRLF, so start from index 2.
        // Sometimes not?
        const newLineDetected = !!((buffer[1] === 0x0a) && (buffer[0] === 0x0d))
        const start = newLineDetected ? 2 : 0
        const part = buffer.slice(start, j - 1)
        const p = { header: header, info: info, part: part }
        allParts.push(process(p))
        buffer = []
        lastline = ''
        state = 5
        header = ''
        info = ''
      } else {
        buffer.push(oneByte)
      }
      if (newLineDetected) lastline = ''
    } else if (state === 5) {
      if (newLineDetected) { state = 1 }
    }
  }
  return allParts
}

//  read the boundary from the content-type header sent by the http client
//  this value may be similar to:
//  'multipart/form-data boundary=----WebKitFormBoundaryvm5A9tzU1ONaGP5B',
export function getBoundary (header) {
  const items = header.split(';')
  if (items) {
    for (let i = 0; i < items.length; i++) {
      const item = (String(items[i])).trim()
      if (item.indexOf('boundary') >= 0) {
        const k = item.split('=')
        return (String(k[1])).trim()
      }
    }
  }
  return ''
}

// Content-Disposition: form-data; name="first_name"
function parseHeader (header) {
  const items = header.split(':')
  return {
    header: items[0],
    value: items2map(items[1])
  }
}

// form-data; name="first_name"
// erzeugt Object mit den ; separierten Werten
function items2map (items) {
  const entries = items.split(';')
  const result = {}
  entries.forEach(e => {
    const parts = e.split('=')
    const name = parts[0].trim()
    let value
    if (parts.length > 1) {
      value = removeQuotation(parts[1].trim())
    }
    result[name] = value
  })
  return result
}

function removeQuotation (str) {
  if (str.charAt(0) === '"' && str.charAt(str.length - 1) === '"') {
    return (str.substr(1, str.length - 2))
  }
  return str
}

function isText (contentType) {
  if (contentType.indexOf('text') > -1) {
    return true
  }
  return false
}

function isJson (contentType) {
  if (contentType.indexOf('json') > -1) {
    return true
  }
  return false
}

