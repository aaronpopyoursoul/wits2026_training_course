import { ref, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

// ================================================================
// useAuth — 認證業務邏輯封裝
// ================================================================

export function useAuth() {
  const userStore = useUserStore()
  const router = useRouter()

  const loginForm = ref({
    email: '',
    password: '',
    rememberMe: false
  })

  const formErrors = ref({
    email: '',
    password: ''
  })

  // --- 計算屬性 ---
  const isAuthenticated = computed(() => userStore.isAuthenticated)
  const currentUser = computed(() => userStore.currentUser)
  const isAdmin = computed(() => userStore.isAdmin)
  const displayName = computed(() => userStore.displayName)
  const isLoading = computed(() => userStore.isLoading)
  const loginError = computed(() => userStore.loginError)

  // --- 操作方法 ---

  function validateForm(): boolean {
    let isValid = true
    formErrors.value = { email: '', password: '' }

    if (!loginForm.value.email) {
      formErrors.value.email = '請輸入電子郵件'
      isValid = false
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginForm.value.email)) {
      formErrors.value.email = '電子郵件格式不正確'
      isValid = false
    }

    if (!loginForm.value.password) {
      formErrors.value.password = '請輸入密碼'
      isValid = false
    } else if (loginForm.value.password.length < 8) {
      formErrors.value.password = '密碼至少需要 8 個字元'
      isValid = false
    }

    return isValid
  }

  async function handleLogin(): Promise<void> {
    if (!validateForm()) return

    const success = await userStore.login(
      loginForm.value.email,
      loginForm.value.password,
      loginForm.value.rememberMe
    )

    if (success) {
      await router.push({ name: 'home' })
    }
  }

  function handleLogout(): void {
    userStore.logout()
    router.push({ name: 'login' })
  }

  function resetForm(): void {
    loginForm.value = { email: '', password: '', rememberMe: false }
    formErrors.value = { email: '', password: '' }
  }

  return {
    loginForm,
    formErrors,
    isAuthenticated,
    currentUser,
    isAdmin,
    displayName,
    isLoading,
    loginError,
    handleLogin,
    handleLogout,
    resetForm
  }
}
