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
  <q-dialog v-model="isDialogVisible" persistent>
    <q-card style="min-width: 400px">
      <q-card-section>
        <div class="text-h6">{{ $t('auth_token') }}</div>
      </q-card-section>

      <q-card-section>
        <q-input
          v-model="authToken"
          :type="isPwd ? 'password' : 'text'"
          :label="$t('auth_token')"
          outlined
          autofocus
          @keyup.enter="submitToken"
          :error="!!errorMessage"
          :error-message="errorMessage"
        >
          <template v-slot:append>
            <q-icon
              :name="isPwd ? 'visibility_off' : 'visibility'"
              class="cursor-pointer"
              @click="isPwd = !isPwd"
            />
          </template>
        </q-input>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat :label="$t('btn_cancel')" color="negative" @click="closeDialog" />
        <q-btn flat :label="$t('btn_login')" color="positive" @click="submitToken" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script lang="ts">
import { defineComponent, PropType, ref, watch } from 'vue'
import { AuthService } from 'src/defs/auth/AuthService'

export default defineComponent({
  name: 'AuthTokenDialog',
  props: {
    modelValue: {
      type: Boolean,
      required: true,
    },
    authService: {
      type: Object as PropType<AuthService>,
      required: true,
    },
  },
  emits: ['update:modelValue', 'submit'],
  setup(props, { emit }) {
    const authToken = ref('')
    const errorMessage = ref('')
    const isDialogVisible = ref(props.modelValue)
    const isPwd = ref(true)

    // Sync isDialogVisible with modelValue
    watch(
      () => props.modelValue,
      (newVal) => {
        isDialogVisible.value = newVal
      }
    )

    watch(isDialogVisible, (newVal) => {
      emit('update:modelValue', newVal)
    })

    const closeDialog = () => {
      isDialogVisible.value = false
      emit('update:modelValue', false)
    }

    const submitToken = () => {
      if (authToken.value.trim() === '') {
        return
      }
      emit('submit', authToken.value)
      props.authService.login(authToken.value)
        .then( () => {
          errorMessage.value = ''
          authToken.value = ''
          closeDialog()
        })
        .catch(err => errorMessage.value = err.message)

    }

    return {
      authToken,
      errorMessage,
      isDialogVisible,
      isPwd,
      closeDialog,
      submitToken,
    }
  }
})
</script>

<style scoped>
.q-card {
  border-radius: 8px;
}
</style>
