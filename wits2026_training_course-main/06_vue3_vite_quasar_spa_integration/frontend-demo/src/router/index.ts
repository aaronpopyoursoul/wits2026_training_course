import { createRouter, createWebHistory } from 'vue-router'

import { pinia } from '@/pinia'
import { setAuthorizationToken } from '@/services/http'
import { useAuthStore } from '@/stores/auth'
import DashboardPage from '@/pages/DashboardPage.vue'
import LoginPage from '@/pages/LoginPage.vue'
import PolicySummaryPage from '@/pages/PolicySummaryPage.vue'
import ClaimStatusPage from '@/pages/ClaimStatusPage.vue'
import NotFoundPage from '@/pages/NotFoundPage.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/login', name: 'login', component: LoginPage },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardPage,
      meta: { requiresAuth: true },
    },
    {
      path: '/policies',
      name: 'policies',
      component: PolicySummaryPage,
      meta: { requiresAuth: true },
    },
    {
      path: '/claims',
      name: 'claims',
      component: ClaimStatusPage,
      meta: { requiresAuth: true },
    },
    { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFoundPage },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore(pinia)
  authStore.hydrate()
  setAuthorizationToken(authStore.accessToken)

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.name === 'login' && authStore.isAuthenticated) {
    return { name: 'dashboard' }
  }

  return true
})