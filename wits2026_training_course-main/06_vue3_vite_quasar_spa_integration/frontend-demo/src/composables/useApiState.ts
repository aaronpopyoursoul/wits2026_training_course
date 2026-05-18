import { ref } from 'vue'

export function useApiState() {
  const isLoading = ref(false)
  const errorMessage = ref('')

  function startLoading() {
    isLoading.value = true
    errorMessage.value = ''
  }

  function finishLoading() {
    isLoading.value = false
  }

  function setError(message: string) {
    errorMessage.value = message
  }

  return {
    isLoading,
    errorMessage,
    startLoading,
    finishLoading,
    setError,
  }
}