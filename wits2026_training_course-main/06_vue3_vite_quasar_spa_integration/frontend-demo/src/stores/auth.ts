import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { loginRequest } from '@/services/auth'
import { setAuthorizationToken } from '@/services/http'
import type { LoginForm, LoginResponse } from '@/types/auth'

const ACCESS_TOKEN_KEY = 'training.accessToken'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref('')
  const tokenType = ref('Bearer')
  const expiresInSeconds = ref<number | null>(null)

  const isAuthenticated = computed(() => accessToken.value.length > 0)

  function hydrate() {
    if (!accessToken.value) {
      accessToken.value = window.localStorage.getItem(ACCESS_TOKEN_KEY) ?? ''
      setAuthorizationToken(accessToken.value)
    }
  }

  function applyLogin(response: LoginResponse) {
    accessToken.value = response.accessToken
    tokenType.value = response.tokenType
    expiresInSeconds.value = response.expiresInSeconds
    window.localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken)
    setAuthorizationToken(response.accessToken)
  }

  async function login(payload: LoginForm) {
    const response = await loginRequest(payload)
    applyLogin(response)
    return response
  }

  function logout() {
    accessToken.value = ''
    expiresInSeconds.value = null
    window.localStorage.removeItem(ACCESS_TOKEN_KEY)
    setAuthorizationToken('')
  }

  return {
    accessToken,
    tokenType,
    expiresInSeconds,
    isAuthenticated,
    hydrate,
    login,
    logout,
  }
})