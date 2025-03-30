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

// composables/useProcessingState.ts
import { ref, onMounted, onUnmounted } from 'vue';
import { getProcessingState } from 'src/defs/api'

export function useProcessingState(pollInterval = 15000) { // Default to 15 seconds
  const processingState = ref<string>('')
  const isLoading = ref<boolean>(false)
  const error = ref<string | null>(null)

  let pollingIntervalId: number | null = null

  const startPolling = () => {
    isLoading.value = true
    pollingIntervalId = window.setInterval(async () => {
      try {
        const data = await getProcessingState()
        processingState.value = data
        error.value = null;
      } catch (err: unknown) {
        if (err instanceof Error) {
          error.value = err.message
        } else {
          error.value = 'An unknown error occurred'
        }
      } finally {
        isLoading.value = false
      }
    }, pollInterval)
  }

  const stopPolling = () => {
    if (pollingIntervalId !== null) {
      clearInterval(pollingIntervalId);
      pollingIntervalId = null;
    }
  }

  onMounted(() => {
    startPolling()
  })

  onUnmounted(() => {
    stopPolling()
  })

  return {
    processingState,
    isLoading,
    error,
    startPolling,
    stopPolling,
  }
}
