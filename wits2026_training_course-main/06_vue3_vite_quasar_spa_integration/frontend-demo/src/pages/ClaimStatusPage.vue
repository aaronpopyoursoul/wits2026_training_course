<template>
  <AppLayout>
    <q-page class="q-pa-lg">
      <PageTitle title="理賠進度查詢" subtitle="輸入理賠單號，查詢理賠案件狀態與承辦人。" />

      <q-card flat bordered class="q-mb-lg">
        <q-card-section class="row q-col-gutter-md items-end">
          <div class="col-12 col-md-6">
            <q-input v-model="claimNo" outlined label="Claim No" />
          </div>
          <div class="col-12 col-md-auto">
            <q-btn color="primary" label="查詢" :loading="isLoading" @click="loadClaim" />
          </div>
        </q-card-section>
      </q-card>

      <q-banner v-if="errorMessage" rounded class="bg-red-1 text-negative q-mb-md">
        {{ errorMessage }}
      </q-banner>

      <q-card v-if="claimStatus" flat bordered>
        <q-card-section>
          <div class="text-h6">查詢結果</div>
        </q-card-section>
        <q-card-section>
          <div>理賠單號：{{ claimStatus.claimNo }}</div>
          <div>狀態：{{ claimStatus.status }}</div>
          <div>承辦人：{{ claimStatus.assignedAdjuster }}</div>
        </q-card-section>
      </q-card>
    </q-page>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { AxiosError } from 'axios'

import AppLayout from '@/layouts/AppLayout.vue'
import PageTitle from '@/components/PageTitle.vue'
import { useApiState } from '@/composables/useApiState'
import { fetchClaimStatus } from '@/services/insurance'
import type { ApiErrorResponse } from '@/types/auth'
import type { ClaimStatusResponse } from '@/types/insurance'

const claimNo = ref('CLM20260511001')
const claimStatus = ref<ClaimStatusResponse | null>(null)
const { errorMessage, finishLoading, isLoading, setError, startLoading } = useApiState()

async function loadClaim() {
  startLoading()
  claimStatus.value = null
  try {
    claimStatus.value = await fetchClaimStatus(claimNo.value)
  } catch (error) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    setError(axiosError.response?.data.message ?? 'query claim failed')
  } finally {
    finishLoading()
  }
}
</script>