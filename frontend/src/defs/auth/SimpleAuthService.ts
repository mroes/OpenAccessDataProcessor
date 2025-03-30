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

// AuthServiceImpl.ts

import { AuthService } from './AuthService'
import { User } from 'components/models'
import { computed, ComputedRef } from 'vue'
import Role from 'src/defs/role'

interface AuthStore {
  isAuthenticated: boolean;
  currentUser: User;
  signIn(authToken: string): void;
  signOut(): void;
}

export class SimpleAuthService implements AuthService {
  private readonly authStore:AuthStore

  constructor(authStore: AuthStore) {
    this.authStore = authStore
  }

  login = async (token: string): Promise<void> => {
    return this.authStore.signIn(token)
  }

  logout = async (): Promise<void> => {
      this.authStore.signOut()
  }

  isAuthenticated = (): boolean => {
    if (!this.authStore) {
      return false
    }
    const authenticated = this.authStore.isAuthenticated
    // console.log('authenticated', authenticated)
    return authenticated
  }

  hasRole = (role: string) => {
    if (!this.authStore || !this.authStore.isAuthenticated) {
      return false
    }
    const user = this.authStore.currentUser
    if (user) {
      return user.role === 'ROLE_' + role || user.role === 'ROLE_' + Role.admin
    } else {
      return false;
    }
  }

  get currentUser(): ComputedRef<User> {
    return computed(() => {
      if (!this.authStore) {
        return {} as User
      }
      return this.authStore.currentUser
    })
  }
}
