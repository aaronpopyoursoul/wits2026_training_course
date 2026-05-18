<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterView, RouterLink } from 'vue-router'
import UserProfile from '@/components/UserProfile.vue'
import { useAuth } from '@/composables/useAuth'
import { useCart } from '@/composables/useCart'

// ================================================================
// App.vue — 應用根元件
// 包含全域導航列與路由容器
// ================================================================

const { isAuthenticated, restoreSession } = useAuth()
const { totalCount } = useCart()

// 頁面載入時嘗試從 localStorage 恢復登入狀態
// （由 useUserStore().restoreSession() 背後處理）
onMounted(() => {
  const { restoreSession: restoreFromStore } = useAuth()
  // 透過 composable 呼叫 store 的 restoreSession
  const userStore = (window as any).__pinia_user_store
  userStore?.restoreSession?.()
})
</script>

<template>
  <div class="app-layout">
    <header class="app-header">
      <nav class="app-nav" aria-label="主要導航">
        <RouterLink class="app-nav__logo" :to="{ name: 'home' }">
          📚 技術書店
        </RouterLink>

        <ul class="app-nav__links" role="list">
          <li><RouterLink :to="{ name: 'home' }">首頁</RouterLink></li>
          <li>
            <RouterLink :to="{ name: 'cart' }" class="cart-link">
              購物車
              <span v-if="totalCount > 0" class="cart-badge" aria-label="`購物車有 ${totalCount} 件商品`">
                {{ totalCount > 99 ? '99+' : totalCount }}
              </span>
            </RouterLink>
          </li>
          <li v-if="!isAuthenticated">
            <RouterLink :to="{ name: 'login' }">登入</RouterLink>
          </li>
        </ul>

        <UserProfile v-if="isAuthenticated" class="app-nav__profile" />
      </nav>
    </header>

    <RouterView />

    <footer class="app-footer">
      <p>Git 教材示範專案 — Vue 3 + TypeScript</p>
    </footer>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: white;
  border-bottom: 1px solid #e5e7eb;
}

.app-nav {
  max-width: 1100px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.app-nav__logo {
  font-size: 1.2rem;
  font-weight: 700;
  text-decoration: none;
  color: inherit;
  margin-right: auto;
}

.app-nav__links {
  display: flex;
  list-style: none;
  gap: 20px;
  margin: 0;
  padding: 0;
}

.app-nav__links a {
  text-decoration: none;
  color: #374151;
  font-size: 0.9rem;
}

.app-nav__links a.router-link-active {
  color: #2563eb;
  font-weight: 600;
}

.cart-link {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.cart-badge {
  background: #ef4444;
  color: white;
  font-size: 0.65rem;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.app-nav__profile {
  margin-left: 16px;
}

.app-footer {
  margin-top: auto;
  text-align: center;
  padding: 20px;
  font-size: 0.8rem;
  color: #9ca3af;
  border-top: 1px solid #f3f4f6;
}
</style>
