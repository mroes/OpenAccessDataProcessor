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

import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      { path: '',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Index.vue') },

      { path: 'insights',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Insights.vue')
      },

      { path: 'coatclassify',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/CoatClassify.vue')
      },

      { path: 'publications',
        name: 'publications',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Publications.vue')
      },
      { path: 'publication/:id',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/PublicationForm.vue')
      },
      { path: 'faq',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Faq.vue')
      },
      { path: 'faq/:id',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Faq.vue')
      },
      { path: 'about',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/About.vue')
      },
      { path: 'impress',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Impress.vue')
      },
      { path: 'upload_pubs',
        name: 'upload_pubs',
        meta: {
          isAuthenticated: true
        },
        component: () => import('pages/UploadPublications.vue')
      },


      { path: 'admin',
        meta: {
          isAdmin: true,
        },
        component: () => import('pages/Admin.vue') },

      { path: 'unauthorized',
        name: 'Unauthorized',
        meta: {
          isAuthenticated: false
        },
        component: () => import('pages/Unauthorized.vue') },
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
]

export default routes
