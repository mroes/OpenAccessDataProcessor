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
import { defineStore } from 'pinia'
import tokenStore from 'src/defs/tokenstore.js'
import { User } from 'components/models'

function extractPayLoad(token: string) {
  let tokenPayload
  if (token) {
    tokenPayload = JSON.parse(window.atob(token.split('.')[1]))
  } else {
    tokenPayload = {}
  }
  return tokenPayload
}

function isAdmin(scopes: Array<string>) {
  let result = false
  if (scopes && scopes.length > 0) {
    result = !!scopes.includes('ROLE_ADMIN')
  }
  return result
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: tokenStore.load() || '',
    jwtRefreshToken: '',
    status: '',
    user: {} as User
  }),
  getters: {
    isAuthenticated: state => !!state.token,
    authStatus: state => state.status,
    currentUser: state => {
      if (!state.user.name && state.token) {
        const payload = extractPayLoad(state.token)
        state.user.name = payload.sub
        state.user.role = payload.role
      }
      return state.user
    }
  },
  actions: {
    async signIn(authToken: string) {
      return new Promise((resolve, reject) => {
        this.status = 'loading'
        axios.post('api/auth/signin',
          {
            'authToken': authToken
          })
          .then(resp => {
            this.token = resp.data
            tokenStore.save(this.token)
            this.status = 'success'
            const payload = extractPayLoad(this.token)
            const admin = isAdmin(payload.scopes)
            this.user = {
              name: payload.sub,
              admin: admin,
              role: payload.role
            }
            resolve(resp)
          })
          .catch(err => {
            this.token = ''
            this.status = 'error'
            this.jwtRefreshToken = ''
            tokenStore.remove()
            reject(err)
          })
      })
    },
    refreshToken() {
      if (!this.refreshToken) {
        // eslint-disable-next-line prefer-promise-reject-errors
        return Promise.reject({ message: 'no refresh token available' })
      }
      return new Promise((resolve, reject) => {
        axios.post('api/auth/token', {
            'refreshToken': this.refreshToken
          },
          { /* skipAuthRefresh: true */ } // no auto interceptor for this call
        )
          .then(resp => {
            console.log('refreshToken ')
            if (resp) {
              console.log('refreshToken ' + resp.data)
              this.token = resp.data.token
              tokenStore.save(this.token)
              resolve(resp)
            } else {
              this.jwtRefreshToken = ''
              tokenStore.remove()
              resolve({})
            }
          })
          .catch(err => {
            this.jwtRefreshToken = ''
            tokenStore.remove()
            reject(err)
          })
      })
    },
    signOut() {
      tokenStore.remove() // if the request fails, remove any user token
      this.status = 'success'
      this.token = ''
      this.user = {} as User
      this.jwtRefreshToken = ''
    }
  }

})

