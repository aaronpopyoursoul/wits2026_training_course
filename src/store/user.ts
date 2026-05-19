// ================================================================
// user.ts.theirs — 同事的修改版本（THEIRS）
//
// 同事在 feature/login-attempt-limit 分支上的改動：
// 為了強化安全性，加入登入錯誤次數統計，
// 連續錯誤 5 次後鎖定帳號 30 分鐘。
//
// 改動摘要：
// 1. 新增 loginAttempts ref 追蹤失敗次數
// 2. 新增 isLocked ref 與 lockUntil ref 追蹤鎖定狀態
// 3. login() 在呼叫 API 前先檢查是否被鎖定
// 4. 登入失敗時累加計數，達 5 次設定鎖定時間戳
// 5. 登入成功後重置計數器
// ================================================================

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, AuthToken, ApiResponse } from '@/types'

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<User | null>(null)
  const token = ref<string | null>(null)
  const isLoading = ref(false)
  const loginError = ref<string | null>(null)

  // ← THEIRS 新增：錯誤次數追蹤狀態
  const loginAttempts = ref(0)
  const isLocked = ref(false)
  const lockUntil = ref<number | null>(null)  // Unix timestamp (ms)

  const isAuthenticated = computed(() => !!token.value && !!currentUser.value)
  const isAdmin = computed(() => currentUser.value?.role === 'admin')
  const displayName = computed(() => currentUser.value?.username ?? '訪客')

  // ← THEIRS 新增：計算剩餘鎖定秒數
  const remainingLockSeconds = computed(() => {
    if (!lockUntil.value) return 0
    const remaining = Math.ceil((lockUntil.value - Date.now()) / 1000)
    return Math.max(0, remaining)
  })

  /**
   * 使用者登入（THEIRS 加入了錯誤次數限制）
   */
  async function login(email: string, password: string): Promise<boolean> {
    // ← THEIRS 新增：鎖定檢查
    if (isLocked.value) {
      if (lockUntil.value && Date.now() < lockUntil.value) {
        loginError.value = `帳號已鎖定，請 ${remainingLockSeconds.value} 秒後再試`
        return false
      }
      // 鎖定時間到了，自動解鎖
      isLocked.value = false
      lockUntil.value = null
      loginAttempts.value = 0
    }

    isLoading.value = true
    loginError.value = null

    try {
      const response = await mockLoginApi(email, password)

      if (!response.success || !response.data) {
        loginError.value = response.message
        loginAttempts.value++  // ← THEIRS 新增：錯誤計數

        // ← THEIRS 新增：達 5 次鎖定 30 分鐘
        if (loginAttempts.value >= 5) {
          isLocked.value = true
          lockUntil.value = Date.now() + 30 * 60 * 1000  // 30 分鐘
          loginError.value = '錯誤次數過多，帳號已鎖定 30 分鐘'
        }
        return false
      }

      // ← THEIRS 新增：登入成功後重置計數
      loginAttempts.value = 0
      isLocked.value = false
      lockUntil.value = null

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
    // ← THEIRS 新增：登出時重置鎖定狀態（管理員手動解鎖用）
    loginAttempts.value = 0
    isLocked.value = false
    lockUntil.value = null
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
    loginAttempts, isLocked, remainingLockSeconds,  // ← THEIRS 新增 export
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
