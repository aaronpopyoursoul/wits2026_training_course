import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, AuthToken, ApiResponse } from '@/types'

// ================================================================
// user store — 管理使用者認證狀態
//
// 【情境三衝突點】
// 此檔案的 login() action 同時被兩位開發者修改：
//   - 你的修改：加入 remember me 功能，將 token 存入 localStorage
//   - 同事的修改：加入登入錯誤次數限制（連續錯誤 5 次鎖定帳號）
// 兩人都修改了同一個 login() 函式，製造了合併衝突。
// ================================================================

export const useUserStore = defineStore('user', () => {
  // --- State ---
  const currentUser = ref<User | null>(null)
  const token = ref<string | null>(null)
  const isLoading = ref(false)
  const loginError = ref<string | null>(null)

  // --- Getters ---
  const isAuthenticated = computed(() => !!token.value && !!currentUser.value)
  const isAdmin = computed(() => currentUser.value?.role === 'admin')
  const displayName = computed(() =>
    currentUser.value?.username ?? '訪客'
  )

  // --- Actions ---

  /**
   * 使用者登入
   *
   * 【你的版本修改點】：加入 rememberMe 參數
   * 若 rememberMe 為 true，將 accessToken 存入 localStorage
   * 使下次造訪時自動保持登入狀態
   */
  async function login(
    email: string,
    password: string,
    rememberMe: boolean = false
  ): Promise<boolean> {
    isLoading.value = true
    loginError.value = null

    try {
      // 模擬 API 呼叫
      const response = await mockLoginApi(email, password)

      if (!response.success || !response.data) {
        loginError.value = response.message
        return false
      }

      const { accessToken, refreshToken } = response.data
      token.value = accessToken

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

  /**
   * 登出並清除所有認證狀態
   */
  function logout(): void {
    currentUser.value = null
    token.value = null
    loginError.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('refresh_token')
  }

  /**
   * 取得目前登入使用者資訊
   */
  async function fetchCurrentUser(): Promise<void> {
    if (!token.value) return

    const response = await mockGetUserApi(token.value)
    if (response.success && response.data) {
      currentUser.value = response.data
    }
  }

  /**
   * 從 localStorage 恢復登入狀態（頁面重整後呼叫）
   */
  function restoreSession(): void {
    const savedToken = localStorage.getItem('auth_token')
    if (savedToken) {
      token.value = savedToken
      fetchCurrentUser()
    }
  }

  return {
    currentUser,
    token,
    isLoading,
    loginError,
    isAuthenticated,
    isAdmin,
    displayName,
    login,
    logout,
    fetchCurrentUser,
    restoreSession
  }
})

// ================================================================
// 模擬 API（實際專案會替換為 axios/fetch 呼叫）
// ================================================================

async function mockLoginApi(
  email: string,
  _password: string
): Promise<ApiResponse<AuthToken>> {
  await delay(300)

  if (email === 'admin@example.com') {
    return {
      success: true,
      data: {
        accessToken: 'mock-access-token-admin-xyz',
        refreshToken: 'mock-refresh-token-admin-xyz',
        expiresIn: 3600
      },
      message: '登入成功'
    }
  }

  return {
    success: false,
    data: null,
    message: '帳號或密碼錯誤',
    errorCode: 'AUTH_INVALID_CREDENTIALS'
  }
}

async function mockGetUserApi(
  _token: string
): Promise<ApiResponse<User>> {
  await delay(200)
  return {
    success: true,
    data: {
      id: 'usr_001',
      username: 'Aaron Chen',
      email: 'admin@example.com',
      role: 'admin',
      avatarUrl: 'https://avatars.example.com/usr_001.jpg',
      createdAt: '2024-01-15T08:00:00Z'
    },
    message: '取得成功'
  }
}

function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}
