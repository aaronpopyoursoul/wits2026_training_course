// ================================================================
// user.ts.ours — 你的修改版本（OURS）
//
// 你在 feature/remember-me 分支上的改動：
// 為 login() 加入 rememberMe 參數，
// 讓使用者可以選擇將 token 存入 localStorage 保持登入狀態。
//
// 改動摘要：
// 1. login() 新增第三個參數 rememberMe: boolean = false
// 2. 登入成功後，若 rememberMe 為 true，存入 localStorage
// 3. logout() 加入清除 localStorage 的邏輯
// 4. 新增 restoreSession() 函式，供頁面重整時呼叫
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
   * 使用者登入（OURS 加入了 rememberMe 參數）
   */
  async function login(
    email: string,
    password: string,
    rememberMe: boolean = false  // ← OURS 新增
  ): Promise<boolean> {
    isLoading.value = true
    loginError.value = null

    try {
      const response = await mockLoginApi(email, password)

      if (!response.success || !response.data) {
        loginError.value = response.message
        return false
      }

      const { accessToken, refreshToken } = response.data
      token.value = accessToken

      // ← OURS 新增：remember me 邏輯
      if (rememberMe) {
        localStorage.setItem('auth_token', accessToken)
        localStorage.setItem('refresh_token', refreshToken)
      }

      await fetchCurrentUser()
      return true
    } catch (err) {
      loginError.value = '網路連線異常，請稍後再試'
      return false
    } finally {
      isLoading.value = false
    }
  }

  // ← OURS 修改：logout 時清除 localStorage
  function logout(): void {
    currentUser.value = null
    token.value = null
    loginError.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('refresh_token')
  }

  async function fetchCurrentUser(): Promise<void> {
    if (!token.value) return
    const response = await mockGetUserApi(token.value)
    if (response.success && response.data) {
      currentUser.value = response.data
    }
  }

  // ← OURS 新增：從 localStorage 恢復登入狀態
  function restoreSession(): void {
    const savedToken = localStorage.getItem('auth_token')
    if (savedToken) {
      token.value = savedToken
      fetchCurrentUser()
    }
  }

  return {
    currentUser, token, isLoading, loginError,
    isAuthenticated, isAdmin, displayName,
    login, logout, fetchCurrentUser, restoreSession  // ← OURS 新增 restoreSession
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
