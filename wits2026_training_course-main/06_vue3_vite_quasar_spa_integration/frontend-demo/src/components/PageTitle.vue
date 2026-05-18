<template>
  <div class="q-mb-md">
    <div class="text-h4 text-weight-bold">{{ title }}</div>
    <div class="text-subtitle2 text-grey-7">{{ subtitle }}</div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  subtitle: string
}>()
</script>