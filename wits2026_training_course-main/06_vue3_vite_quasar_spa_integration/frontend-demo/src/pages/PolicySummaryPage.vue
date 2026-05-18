<template>
  <AppLayout>
    <q-page class="q-pa-lg">
      <PageTitle title="保單摘要查詢" subtitle="輸入保單號，呼叫 04 JWT API 查詢保單資料。" />

      <q-card flat bordered class="q-mb-lg">
        <q-card-section class="row q-col-gutter-md items-end">
          <div class="col-12 col-md-6">
            <q-input v-model="policyNo" outlined label="Policy No" />
          </div>
          <div class="col-12 col-md-auto">
            <q-btn color="primary" label="查詢" :loading="isLoading" @click="loadPolicy" />
          </div>
        </q-card-section>
      </q-card>

      <q-banner v-if="errorMessage" rounded class="bg-red-1 text-negative q-mb-md">
        {{ errorMessage }}
      </q-banner>

      <q-card v-if="policySummary" flat bordered>
        <q-card-section>
          <div class="text-h6">查詢結果</div>
        </q-card-section>
        <q-card-section>
          <div>保單號：{{ policySummary.policyNo }}</div>
          <div>保戶：{{ policySummary.policyHolderName }}</div>
          <div>商品代碼：{{ policySummary.productCode }}</div>
          <div>狀態：{{ policySummary.policyStatus }}</div>
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
import { fetchPolicySummary } from '@/services/insurance'
import type { ApiErrorResponse } from '@/types/auth'
import type { PolicySummaryResponse } from '@/types/insurance'

const policyNo = ref('POL20260001')
const policySummary = ref<PolicySummaryResponse | null>(null)
const { errorMessage, finishLoading, isLoading, setError, startLoading } = useApiState()

async function loadPolicy() {
  startLoading()
  policySummary.value = null
  try {
    policySummary.value = await fetchPolicySummary(policyNo.value)
  } catch (error) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    setError(axiosError.response?.data.message ?? 'query policy failed')
  } finally {
    finishLoading()
  }
}
</script>