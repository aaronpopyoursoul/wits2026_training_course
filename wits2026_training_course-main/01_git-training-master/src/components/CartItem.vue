<script setup lang="ts">
import type { CartItem } from '@/types'
import { useCart } from '@/composables/useCart'

// ================================================================
// CartItem.vue — 購物車清單中的單筆商品元件
// ================================================================

interface Props {
  item: CartItem
}

const props = defineProps<Props>()
const { updateItemQuantity, removeFromCart } = useCart()

function onQuantityChange(event: Event): void {
  const input = event.target as HTMLInputElement
  const quantity = parseInt(input.value, 10)
  if (!isNaN(quantity)) {
    updateItemQuantity(props.item.product.id, quantity)
  }
}
</script>

<template>
  <div class="cart-item">
    <img
      :src="item.product.imageUrl ?? '/placeholder.png'"
      :alt="item.product.name"
      class="cart-item__image"
    />

    <div class="cart-item__info">
      <h3 class="cart-item__name">{{ item.product.name }}</h3>
      <p class="cart-item__price">
        NT$ {{ (item.product.price / 100).toLocaleString('zh-TW') }}
      </p>
    </div>

    <div class="cart-item__controls">
      <button
        class="btn btn--icon"
        :disabled="item.quantity <= 1"
        @click="updateItemQuantity(item.product.id, item.quantity - 1)"
        aria-label="減少數量"
      >
        −
      </button>

      <input
        type="number"
        :value="item.quantity"
        :min="1"
        :max="item.product.stock"
        class="cart-item__qty-input"
        aria-label="商品數量"
        @change="onQuantityChange"
      />

      <button
        class="btn btn--icon"
        :disabled="item.quantity >= item.product.stock"
        @click="updateItemQuantity(item.product.id, item.quantity + 1)"
        aria-label="增加數量"
      >
        ＋
      </button>
    </div>

    <p class="cart-item__subtotal">
      NT$ {{ ((item.product.price * item.quantity) / 100).toLocaleString('zh-TW') }}
    </p>

    <button
      class="btn btn--ghost cart-item__remove"
      @click="removeFromCart(item.product.id)"
      aria-label="移除此商品"
    >
      移除
    </button>
  </div>
</template>

<style scoped>
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #e5e7eb;
}

.cart-item__image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  background: #f3f4f6;
}

.cart-item__info {
  flex: 1;
}

.cart-item__name {
  font-size: 0.95rem;
  font-weight: 500;
  margin: 0 0 4px;
}

.cart-item__price {
  font-size: 0.85rem;
  color: #6b7280;
  margin: 0;
}

.cart-item__controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cart-item__qty-input {
  width: 48px;
  text-align: center;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  padding: 4px;
}

.cart-item__subtotal {
  font-weight: 600;
  min-width: 80px;
  text-align: right;
  margin: 0;
}

.btn {
  cursor: pointer;
  border: none;
  background: none;
  padding: 4px 8px;
  border-radius: 4px;
}

.btn--icon {
  font-size: 1.1rem;
  background: #f3f4f6;
}

.btn--icon:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn--ghost {
  font-size: 0.8rem;
  color: #ef4444;
}

.btn--ghost:hover {
  background: #fee2e2;
}
</style>
