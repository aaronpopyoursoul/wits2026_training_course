<script setup lang="ts">
import { useAuth } from '@/composables/useAuth'
import { useRouter } from 'vue-router'

// ================================================================
// LoginView.vue — 登入頁面
// ================================================================

const router = useRouter()
const {
  loginForm,
  formErrors,
  isLoading,
  loginError,
  handleLogin
} = useAuth()

async function onSubmit(): Promise<void> {
  await handleLogin()
}
</script>

<template>
  <main class="login-view">
    <div class="login-card">
      <h1 class="login-card__title">登入帳號</h1>

      <form class="login-card__form" @submit.prevent="onSubmit">
        <div class="form-group">
          <label for="email">電子郵件</label>
          <input
            id="email"
            v-model="loginForm.email"
            type="email"
            autocomplete="username"
            placeholder="your@email.com"
            :class="{ 'input--error': formErrors.email }"
          />
          <p v-if="formErrors.email" class="form-error">{{ formErrors.email }}</p>
        </div>

        <div class="form-group">
          <label for="password">密碼</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            autocomplete="current-password"
            placeholder="••••••••"
            :class="{ 'input--error': formErrors.password }"
          />
          <p v-if="formErrors.password" class="form-error">{{ formErrors.password }}</p>
        </div>

        <div class="form-group form-group--checkbox">
          <label class="checkbox-label">
            <input
              v-model="loginForm.rememberMe"
              type="checkbox"
            />
            記住我（30 天內自動登入）
          </label>
        </div>

        <p v-if="loginError" class="form-error form-error--banner" role="alert">
          {{ loginError }}
        </p>

        <!-- 教學提示：測試帳號 -->
        <p class="login-card__hint">
          測試帳號：<code>admin@example.com</code> ／ 任意密碼（8 字元以上）
        </p>

        <button
          type="submit"
          class="btn btn--primary"
          :disabled="isLoading"
        >
          {{ isLoading ? '驗證中...' : '登入' }}
        </button>
      </form>
    </div>
  </main>
</template>

<style scoped>
.login-view {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 32px;
  background: white;
}

.login-card__title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  text-align: center;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 6px;
}

.form-group input[type="email"],
.form-group input[type="password"] {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 9px 12px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.input--error {
  border-color: #ef4444 !important;
}

.form-group--checkbox {
  display: flex;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
  cursor: pointer;
}

.form-error {
  color: #ef4444;
  font-size: 0.8rem;
  margin-top: 4px;
}

.form-error--banner {
  background: #fee2e2;
  border: 1px solid #fca5a5;
  border-radius: 6px;
  padding: 8px 12px;
  margin-top: 0;
  font-size: 0.88rem;
}

.login-card__hint {
  font-size: 0.78rem;
  color: #9ca3af;
  margin-bottom: 16px;
}

.btn--primary {
  width: 100%;
  padding: 10px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
}

.btn--primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
