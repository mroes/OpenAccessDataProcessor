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

import { boot } from 'quasar/wrappers'
import { Composer, createI18n } from 'vue-i18n'

import messages from 'src/i18n'

// Create a function that returns the `t` function from Composer
export let translate: Composer['t']

export let locale: string

const supportedLanguages = ['de', 'en']
const defaultLanguage = 'de'
export default boot(({ app }) => {
  const browserLanguage = navigator.language.split('-')[0]
  const language = supportedLanguages.includes(browserLanguage) ? browserLanguage : defaultLanguage;

  const i18n = createI18n({
    legacy: false,
    locale: language,
    fallbackLocale: defaultLanguage,
    globalInjection: true,
    messages,
  })

  locale = i18n.global.locale.value

  // Explicitly cast `i18n.global` as `Composer`
  const composer = i18n.global as unknown as Composer

  // Assign the `t` function from Composer to the `translate` variable
  translate = composer.t

  // Set i18n instance on app
  app.use(i18n)
})
