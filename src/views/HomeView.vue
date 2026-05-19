<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Product } from '@/types'

// ================================================================
// HomeView.vue — 首頁：展示商品列表
// ================================================================

const products = ref<Product[]>([
  {
    id: 'prod_001',
    name: 'TypeScript 深度實戰',
    description: '從型別系統到設計模式，系統性掌握 TS 開發',
    price: 89000,
    stock: 50,
    imageUrl: '/images/book-ts.jpg',
    categoryId: 'cat_books'
  },
  {
    id: 'prod_002',
    name: 'Vue 3 企業級開發',
    description: 'Composition API、Pinia、效能優化實戰',
    price: 79000,
    stock: 30,
    imageUrl: '/images/book-vue.jpg',
    categoryId: 'cat_books'
  },
  {
    id: 'prod_003',
    name: 'Git 協作工作流',
    description: '分支策略、衝突解決、CI/CD 整合完整指南',
    price: 69000,
    stock: 100,
    imageUrl: '/images/book-git.jpg',
    categoryId: 'cat_books'
  }
])

const isLoading = ref(false)

onMounted(() => {
  // 實際場景：呼叫 API 取得商品列表
  // await productStore.fetchProducts()
  console.log('[HomeView] 頁面載入，共 %d 件商品', products.value.length)
})
</script>

<template>
  <main class="home-view">
    <section class="home-view__hero">
      <h1 class="home-view__heading">技術書店</h1>
      <p class="home-view__sub">精選程式設計 × DevOps 實戰書籍</p>
    </section>

    <section class="home-view__products">
      <div v-if="isLoading" class="loading">載入中...</div>

      <ul v-else class="product-grid" role="list">
        <li
          v-for="product in products"
          :key="product.id"
          class="product-card"
        >
          <img
            :src="product.imageUrl ?? '/placeholder.png'"
            :alt="product.name"
            class="product-card__image"
          />
          <div class="product-card__body">
            <h2 class="product-card__name">{{ product.name }}</h2>
            <p class="product-card__desc">{{ product.description }}</p>
            <footer class="product-card__footer">
              <span class="product-card__price">
                NT$ {{ (product.price / 100).toLocaleString('zh-TW') }}
              </span>
              <router-link
                :to="{ name: 'cart' }"
                class="btn btn--primary"
              >
                加入購物車
              </router-link>
            </footer>
          </div>
        </li>
      </ul>
    </section>
  </main>
</template>

<style scoped>
.home-view__hero {
  text-align: center;
  padding: 48px 24px;
}

.home-view__heading {
  font-size: 2rem;
  font-weight: 700;
}

.home-view__sub {
  color: #6b7280;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
  list-style: none;
  padding: 0 24px 48px;
  margin: 0;
}

.product-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

.product-card__image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  background: #f3f4f6;
}

.product-card__body {
  padding: 16px;
}

.product-card__name {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 8px;
}

.product-card__desc {
  font-size: 0.85rem;
  color: #6b7280;
  margin: 0 0 16px;
}

.product-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-card__price {
  font-weight: 700;
  color: #1d4ed8;
}

.btn--primary {
  background: #2563eb;
  color: white;
  padding: 6px 14px;
  border-radius: 6px;
  text-decoration: none;
  font-size: 0.85rem;
}
</style>
