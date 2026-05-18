import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'

import App from './App.vue'
import HomeView from './views/HomeView.vue'
import CartView from './views/CartView.vue'
import LoginView from './views/LoginView.vue'

// ================================================================
// main.ts — 應用程式入口
//
// 職責：
// 1. 建立 Vue app 實例
// 2. 掛載 Pinia（狀態管理）
// 3. 掛載 Vue Router（路由）
// 4. 掛載應用
// ================================================================

// --- 路由設定 ---
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
      // 注意：實際專案可在此加入 beforeEnter 驗證登入狀態
      // beforeEnter: (to, from, next) => { ... }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      // 萬用路由：404 redirect 回首頁
      path: '/:pathMatch(.*)*',
      redirect: { name: 'home' }
    }
  ]
})

// --- Pinia 導航守衛（示範用途）---
router.beforeEach((to, _from) => {
  // 未來可在此加入全域的認證檢查
  // 例如：if (requiresAuth(to) && !isAuthenticated()) return '/login'
  return true
})

// --- 建立與掛載應用 ---
const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

app.mount('#app')
