<template>
  <q-page class="row items-center justify-center bg-grey-2 q-pa-lg">
    <q-card flat bordered style="width: 420px; max-width: 100%">
      <q-card-section>
        <div class="text-h5 text-weight-bold">課程前台登入</div>
        <div class="text-subtitle2 text-grey-7">對接 04 的 JWT API，進入保單與理賠查詢前台。</div>
      </q-card-section>

      <q-card-section>
        <q-form class="column q-gutter-md" @submit.prevent="submitLogin">
          <q-input v-model="form.username" outlined label="Username" />
          <q-input v-model="form.password" outlined label="Password" type="password" />
          <q-banner v-if="errorMessage" rounded dense class="bg-red-1 text-negative">
            {{ errorMessage }}
          </q-banner>
          <q-btn color="primary" label="登入" type="submit" :loading="isLoading" />
        </q-form>
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AxiosError } from 'axios'

import { useApiState } from '@/composables/useApiState'
import { pinia } from '@/pinia'
import { useAuthStore } from '@/stores/auth'
import type { ApiErrorResponse } from '@/types/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore(pinia)
const { errorMessage, finishLoading, isLoading, setError, startLoading } = useApiState()

const form = reactive({
  username: 'trainer',
  password: 'P@ssw0rd123',
})

async function submitLogin() {
  startLoading()
  try {
    await authStore.login(form)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    await router.push(redirect)
  } catch (error) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    setError(axiosError.response?.data.message ?? 'login failed')
  } finally {
    finishLoading()
  }
}
</script>