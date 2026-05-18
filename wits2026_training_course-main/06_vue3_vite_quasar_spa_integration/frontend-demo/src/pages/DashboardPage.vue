<template>
  <AppLayout>
    <q-page class="q-pa-lg">
      <PageTitle
        title="專題前台首頁"
        subtitle="這個頁面示範 JWT 登入後的受保護路由與 Quasar 版型。"
      />

      <div class="row q-col-gutter-md">
        <div class="col-12 col-md-6">
          <q-card flat bordered>
            <q-card-section>
              <div class="text-h6">保單查詢</div>
              <div class="text-body2 text-grey-7">進入 Vue 頁面查詢保單摘要，對接既有 Spring Boot API。</div>
            </q-card-section>
          </q-card>
        </div>
        <div class="col-12 col-md-6">
          <q-card flat bordered>
            <q-card-section>
              <div class="text-h6">理賠進度</div>
              <div class="text-body2 text-grey-7">示範 JWT header、API 呼叫與錯誤狀態顯示。</div>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </q-page>
  </AppLayout>
</template>

<script setup lang="ts">
import AppLayout from '@/layouts/AppLayout.vue'
import PageTitle from '@/components/PageTitle.vue'
</script>