<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-toolbar-title>Insurance Training SPA</q-toolbar-title>
        <q-btn flat dense label="Dashboard" :to="{ name: 'dashboard' }" />
        <q-btn flat dense label="Policies" :to="{ name: 'policies' }" />
        <q-btn flat dense label="Claims" :to="{ name: 'claims' }" />
        <q-space />
        <q-btn flat dense label="Logout" @click="handleLogout" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <slot />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

import { pinia } from '@/pinia'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore(pinia)

function handleLogout() {
  authStore.logout()
  void router.push({ name: 'login' })
}
</script>