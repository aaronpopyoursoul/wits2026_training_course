import type { Router } from 'vue-router'

import { pinia } from '@/pinia'
import { registerUnauthorizedHandler } from '@/services/http'
import { useAuthStore } from '@/stores/auth'

export function registerAuthBootstrap(router: Router) {
  const authStore = useAuthStore(pinia)
  authStore.hydrate()

  registerUnauthorizedHandler(() => {
    authStore.logout()
    void router.push({ name: 'login' })
  })
}