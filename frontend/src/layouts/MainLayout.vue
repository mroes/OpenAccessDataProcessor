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
  <q-layout view="hHh Lpr fFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          {{ $t('application_title') }}
        </q-toolbar-title>

        <span>
          <q-btn flat round icon="login" v-if="!authService.isAuthenticated()" @click="signIn">
             <q-tooltip class="text-body2" :delay="500">
                <div class="">{{ $t('btn_login') }}</div>
            </q-tooltip>
        </q-btn>
        <div v-else>
          {{ authService.currentUser.value.name }}
          <q-btn flat round icon="power_settings_new" @click="authService.logout" />
        </div>
        </span>
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
    >
      <q-list>
        <q-item to="/">
          <q-item-section avatar>
            <q-icon name="fas fa-home" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_home') }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-item to="/insights">
          <q-item-section avatar>
            <q-icon name="fas fa-magnifying-glass" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_insights') }}</q-item-label>
          </q-item-section>
        </q-item>

          <q-item to="/publications">
          <q-item-section avatar>
            <q-icon name="fa-solid fa-book" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_publications') }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-item to="/coatclassify">
          <q-item-section avatar>
            <q-icon name="fas fa-glasses" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_coat_classify') }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-item to="/faq">
          <q-item-section avatar>
            <q-icon name="fa-regular fa-circle-question" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_faq') }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-item to="/about">
          <q-item-section avatar>
            <q-icon name="fas fa-info-circle" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_about') }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-separator />

        <q-item v-if="authService.hasRole(Role.upload)" to="/upload_pubs">
          <q-item-section avatar>
            <q-icon name="fa-solid fa-cloud-arrow-up" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_self_service') }}</q-item-label>
          </q-item-section>
        </q-item>

        <q-item v-if="authService.hasRole(Role.admin)" to="/admin">
          <q-item-section avatar>
            <q-icon name="fa-solid fa-gear" />
          </q-item-section>
          <q-item-section>
            <q-item-label>{{ $t('menu_admin') }}</q-item-label>
          </q-item-section>
        </q-item>

      </q-list>

    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>

    <q-footer class="mfooter">
      <q-toolbar style="min-height: 26px" >
        <span><a href="/impress" target="_new" style="color: white">{{ $t('impress') }}</a></span>
        <span><a href="https://www.at2oa.at/at2oa2_home.html#bookmark-tp2" target="_new" style="color: white; margin-left: 30px">{{ $t('project_title') }}</a></span>
        <span><a href="swagger-ui/index.html" target="_new" style="color: white; margin-left: 30px">{{ $t('api_link') }}</a></span>
        <q-space />
        <span>{{ $t('version') }} {{ appVersion.version }}, developed by <a href="https://www.roesel.at" target="_new" style="color: white">roesel.at</a></span>
      </q-toolbar>
    </q-footer>

  </q-layout>
  <auth-token-dialog
    v-model="isDialogVisible"
    :authService="authService"
  />
</template>

<script lang="ts">

import Role from 'src/defs/role'

import {defineComponent, ref} from 'vue'
import appVersion from 'src/defs/version'
import { useAuthStore } from 'stores/auth-store'
import { SimpleAuthService } from 'src/defs/auth/SimpleAuthService'
import AuthTokenDialog from 'components/AuthTokenDialog.vue'

export default defineComponent({
  name: 'MainLayout',

  components: {
    AuthTokenDialog
  },

  setup () {
    const leftDrawerOpen = ref(false)
    const isDialogVisible = ref(false);

    const authStore = useAuthStore()
    const authService = new SimpleAuthService(authStore)

    const signIn = () => {
      isDialogVisible.value = true
    }

    return {
      leftDrawerOpen,
      appVersion: appVersion,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      },
      isDialogVisible,
      signIn,
      authService,
      Role
    }
  }
})
</script>

<style>
</style>
