<script setup lang="ts">
import { ref } from 'vue'
import CartItem from '@/components/CartItem.vue'
import CheckoutForm from '@/components/CheckoutForm.vue'
import { useCart } from '@/composables/useCart'
import type { Order } from '@/types'

// ================================================================
// CartView.vue — 購物車頁面
// ================================================================

const { cartItems, isEmpty, formattedTotal, hasFreeShipping } = useCart()

const showCheckoutForm = ref(false)
const completedOrder = ref<Order | null>(null)

function handleOrderPlaced(order: Order): void {
  completedOrder.value = order
  showCheckoutForm.value = false
}
</script>

<template>
  <main class="cart-view">
    <!-- 訂單完成提示 -->
    <div v-if="completedOrder" class="cart-view__success" role="status">
      <h2>訂單已成立！</h2>
      <p>訂單編號：<strong>{{ completedOrder.id }}</strong></p>
      <p>應付金額：<strong>NT$ {{ (completedOrder.total / 100).toLocaleString('zh-TW') }}</strong></p>
      <router-link :to="{ name: 'home' }" class="btn btn--primary">繼續購物</router-link>
    </div>

    <!-- 空購物車 -->
    <div v-else-if="isEmpty && !showCheckoutForm" class="cart-view__empty">
      <p>購物車是空的</p>
      <router-link :to="{ name: 'home' }" class="btn btn--primary">前往選購</router-link>
    </div>

    <!-- 購物車清單 -->
    <div v-else-if="!showCheckoutForm" class="cart-view__list">
      <h1 class="cart-view__title">購物車</h1>

      <CartItem
        v-for="item in cartItems"
        :key="item.product.id"
        :item="item"
      />

      <div class="cart-view__summary">
        <p v-if="hasFreeShipping" class="cart-view__free-ship">
          本單享免運費
        </p>
        <dl class="cart-view__totals">
          <dt>小計</dt>
          <dd>{{ formattedTotal.subtotal }}</dd>
          <dt>運費</dt>
          <dd>{{ formattedTotal.shippingFee }}</dd>
          <dt class="total-label">合計</dt>
          <dd class="total-amount">{{ formattedTotal.total }}</dd>
        </dl>
        <button class="btn btn--primary" @click="showCheckoutForm = true">
          前往結帳
        </button>
      </div>
    </div>

    <!-- 結帳表單 -->
    <CheckoutForm
      v-else
      @order-placed="handleOrderPlaced"
      @checkout-cancelled="showCheckoutForm = false"
    />
  </main>
</template>

<style scoped>
.cart-view {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px;
}

.cart-view__title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 16px;
}

.cart-view__summary {
  margin-top: 24px;
  text-align: right;
}

.cart-view__free-ship {
  color: #16a34a;
  font-size: 0.85rem;
  margin-bottom: 8px;
}

.cart-view__totals {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 4px 16px;
  justify-items: end;
  margin-bottom: 16px;
}

.total-label,
.total-amount {
  font-weight: 700;
  font-size: 1.1rem;
  padding-top: 8px;
  border-top: 1px solid #e5e7eb;
}

.cart-view__empty,
.cart-view__success {
  text-align: center;
  padding: 60px 24px;
}

.btn--primary {
  background: #2563eb;
  color: white;
  padding: 10px 24px;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-size: 0.95rem;
  text-decoration: none;
  display: inline-block;
}
</style>
