// ================================================================
// user.ts.base — 衝突前的共同祖先版本
//
// 這是衝突發生前雙方都認同的版本（三向合併的 BASE）。
// 此版本的 login() 函式只有基本的登入邏輯，
// 沒有 rememberMe 功能，也沒有錯誤次數限制。
// ================================================================

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, AuthToken, ApiResponse } from '@/types'

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<User | null>(null)
  const token = ref<string | null>(null)
  const isLoading = ref(false)
  const loginError = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value && !!currentUser.value)
  const isAdmin = computed(() => currentUser.value?.role === 'admin')
  const displayName = computed(() => currentUser.value?.username ?? '訪客')

  /**
   * 基本登入函式（BASE 版本）
   * 沒有 rememberMe，沒有錯誤次數統計
   */
  async function login(email: string, password: string): Promise<boolean> {
    isLoading.value = true
    loginError.value = null

    try {
      const response = await mockLoginApi(email, password)

      if (!response.success || !response.data) {
        loginError.value = response.message
        return false
      }

      token.value = response.data.accessToken
      await fetchCurrentUser()
      return true
    } catch (err) {
      loginError.value = '網路連線異常，請稍後再試'
      return false
    } finally {
      isLoading.value = false
    }
  }

  function logout(): void {
    currentUser.value = null
    token.value = null
    loginError.value = null
  }

  async function fetchCurrentUser(): Promise<void> {
    if (!token.value) return
    const response = await mockGetUserApi(token.value)
    if (response.success && response.data) {
      currentUser.value = response.data
    }
  }

  return {
    currentUser, token, isLoading, loginError,
    isAuthenticated, isAdmin, displayName,
    login, logout, fetchCurrentUser
  }
})

async function mockLoginApi(email: string, _password: string): Promise<ApiResponse<AuthToken>> {
  await new Promise(r => setTimeout(r, 300))
  if (email === 'admin@example.com') {
    return { success: true, data: { accessToken: 'mock-token', refreshToken: 'mock-refresh', expiresIn: 3600 }, message: '登入成功' }
  }
  return { success: false, data: null, message: '帳號或密碼錯誤', errorCode: 'AUTH_INVALID_CREDENTIALS' }
}

async function mockGetUserApi(_token: string): Promise<ApiResponse<User>> {
  await new Promise(r => setTimeout(r, 200))
  return { success: true, data: { id: 'usr_001', username: 'Aaron Chen', email: 'admin@example.com', role: 'admin', createdAt: '2024-01-15T08:00:00Z' }, message: '取得成功' }
}
