<script setup lang="ts">
import { useAuth } from '@/composables/useAuth'

// ================================================================
// UserProfile.vue — 顯示目前登入使用者的頭像與基本資訊
// ================================================================

const { currentUser, displayName, isAdmin, handleLogout } = useAuth()
</script>

<template>
  <div class="user-profile">
    <div v-if="currentUser" class="user-profile__card">
      <img
        :src="currentUser.avatarUrl ?? '/avatar-placeholder.png'"
        :alt="`${displayName} 的頭像`"
        class="user-profile__avatar"
      />
      <div class="user-profile__info">
        <p class="user-profile__name">{{ displayName }}</p>
        <p class="user-profile__email">{{ currentUser.email }}</p>
        <span
          class="user-profile__badge"
          :class="{
            'user-profile__badge--admin': isAdmin,
            'user-profile__badge--member': !isAdmin
          }"
        >
          {{ isAdmin ? '管理員' : '一般會員' }}
        </span>
      </div>
      <button class="btn btn--logout" @click="handleLogout">登出</button>
    </div>

    <div v-else class="user-profile__placeholder">
      <p>尚未登入</p>
    </div>
  </div>
</template>

<style scoped>
.user-profile__card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
}

.user-profile__avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  background: #e5e7eb;
}

.user-profile__info {
  flex: 1;
}

.user-profile__name {
  font-weight: 600;
  margin: 0;
}

.user-profile__email {
  font-size: 0.8rem;
  color: #6b7280;
  margin: 2px 0 4px;
}

.user-profile__badge {
  display: inline-block;
  font-size: 0.7rem;
  padding: 2px 8px;
  border-radius: 12px;
}

.user-profile__badge--admin {
  background: #dbeafe;
  color: #1d4ed8;
}

.user-profile__badge--member {
  background: #f3f4f6;
  color: #374151;
}

.btn--logout {
  border: 1px solid #e5e7eb;
  background: white;
  color: #374151;
  padding: 6px 14px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
}

.btn--logout:hover {
  background: #fee2e2;
  border-color: #fca5a5;
  color: #dc2626;
}
</style>
