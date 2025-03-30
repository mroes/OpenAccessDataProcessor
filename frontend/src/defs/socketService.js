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

import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'

const topic = '/topic/command'
const userQueue = '/user/queue/messages'

function sessionFrom(url) {
  // console.log(url)
  let lastSlash = url.lastIndexOf('/')
  let str = url.substring(0, lastSlash)
  lastSlash = str.lastIndexOf('/')
  str = str.substring(lastSlash+1)
  return str
}

const SocketService = {

  connected: false,
  socket: undefined,
  stompClient: undefined,
  sessionId: undefined,

  connect: function (userId, userQueueCallback, topicCallback, server) {
    return new Promise((resolve, reject) => {
      if (!server) {
        server = window.location.origin
      }
      server += '/websock'
      // console.log('websocket server = ' + server)
      this.socket = new SockJS(server)
      this.socket.onerror = function(event) {
        console.error('SockJS connection error:', event)
        // Handle the error, update UI, etc.
        SocketService.connected = false
      }
      this.socket.onclose = function(event) {
        console.log('SockJS close:', event)
      }

      this.stompClient = Stomp.over(this.socket,
        {
          debug: false,
          protocols: ['v12.stomp']
        })

      try {
        this.stompClient.connect(
          { user: userId },
          (/* frame */) => {
            if (userQueueCallback) {
              this.stompClient.subscribe(userQueue, tick => userQueueCallback(JSON.parse(tick.body)))
            }
            if (topicCallback) {
              this.stompClient.subscribe(topic, tick => topicCallback(JSON.parse(tick.body)))
            }
            // console.log('connected is set to true')
            this.connected = true

            this.sessionId = sessionFrom(this.stompClient.ws._transport.url)
            resolve(this.sessionId)
          },
          error => {
            console.error('stomp connect error', error)
            this.connected = false
            reject(error)
          }
        )
      } catch (e) {
        console.error(e)
        this.connected = false
        reject(error)
      }
    })
  },

  disconnect () {
    if (this.connected && this.stompClient) {
      this.stompClient.disconnect()
    }
    this.connected = false
  },

  tickleConnection () {
    this.connected ? this.disconnect() : this.connect()
  },

  isConnected () {
    return this.connected
  },

  send (sendMessage) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send('/app/command', JSON.stringify(sendMessage), {})
    }
  }

}

export default SocketService
