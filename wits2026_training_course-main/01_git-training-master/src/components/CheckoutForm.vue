<script setup lang="ts">
import { ref } from 'vue'
import { useCart } from '@/composables/useCart'
import type { ShippingAddress, Order } from '@/types'

// ================================================================
// CheckoutForm.vue — 結帳表單元件
//
// 【情境一熱修復背景】
// 此元件顯示的「訂單金額」直接來自 useCart().formattedTotal。
// 由於 cart store 的 calcTotal 有 Bug（未乘以 quantity），
// 這裡顯示的小計金額是錯誤的。
// 修復 cart store 後，此元件無需修改，金額自動正確。
// ================================================================

const emit = defineEmits<{
  orderPlaced: [order: Order]
  checkoutCancelled: []
}>()

const { formattedTotal, isEmpty, isCheckingOut } = useCart()
const { submitOrder } = useCart()

const form = ref<ShippingAddress>({
  recipientName: '',
  phone: '',
  zipCode: '',
  city: '',
  district: '',
  addressLine: ''
})

const isSubmitting = ref(false)
const submitError = ref<string | null>(null)

async function handleSubmit(): Promise<void> {
  submitError.value = null
  isSubmitting.value = true

  try {
    const order = await submitOrder(form.value)
    if (order) {
      emit('orderPlaced', order)
    } else {
      submitError.value = '訂單建立失敗，請稍後再試'
    }
  } catch {
    submitError.value = '網路異常，請確認連線狀態'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="checkout-form">
    <h2 class="checkout-form__title">填寫收件資訊</h2>

    <div class="checkout-form__summary">
      <dl class="price-list">
        <div class="price-list__row">
          <dt>商品小計</dt>
          <dd>{{ formattedTotal.subtotal }}</dd>
        </div>
        <div class="price-list__row">
          <dt>運費</dt>
          <dd>{{ formattedTotal.shippingFee }}</dd>
        </div>
        <div class="price-list__row price-list__row--total">
          <dt>應付金額</dt>
          <dd>{{ formattedTotal.total }}</dd>
        </div>
      </dl>
    </div>

    <form class="checkout-form__fields" @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="recipient-name">收件人姓名</label>
        <input
          id="recipient-name"
          v-model="form.recipientName"
          type="text"
          required
          placeholder="王小明"
        />
      </div>

      <div class="form-group">
        <label for="phone">聯絡電話</label>
        <input
          id="phone"
          v-model="form.phone"
          type="tel"
          required
          placeholder="0912345678"
        />
      </div>

      <div class="form-group form-group--row">
        <div>
          <label for="city">縣市</label>
          <input id="city" v-model="form.city" type="text" required placeholder="台北市" />
        </div>
        <div>
          <label for="district">行政區</label>
          <input id="district" v-model="form.district" type="text" required placeholder="信義區" />
        </div>
        <div>
          <label for="zip">郵遞區號</label>
          <input id="zip" v-model="form.zipCode" type="text" required placeholder="110" />
        </div>
      </div>

      <div class="form-group">
        <label for="address">詳細地址</label>
        <input
          id="address"
          v-model="form.addressLine"
          type="text"
          required
          placeholder="信義路五段 XXX 號"
        />
      </div>

      <p v-if="submitError" class="form-error" role="alert">
        {{ submitError }}
      </p>

      <div class="checkout-form__actions">
        <button
          type="button"
          class="btn btn--outline"
          @click="emit('checkoutCancelled')"
        >
          返回購物車
        </button>
        <button
          type="submit"
          class="btn btn--primary"
          :disabled="isEmpty || isSubmitting || isCheckingOut"
        >
          {{ isSubmitting ? '處理中...' : '確認下單' }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.checkout-form {
  max-width: 560px;
  margin: 0 auto;
  padding: 24px;
}

.checkout-form__title {
  font-size: 1.4rem;
  font-weight: 600;
  margin-bottom: 20px;
}

.price-list {
  background: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 24px;
}

.price-list__row {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 0.9rem;
}

.price-list__row--total {
  border-top: 1px solid #e5e7eb;
  margin-top: 8px;
  padding-top: 12px;
  font-weight: 700;
  font-size: 1rem;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 6px;
  color: #374151;
}

.form-group input {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 8px 12px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.form-group--row {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 12px;
}

.form-error {
  color: #ef4444;
  font-size: 0.85rem;
  margin-bottom: 12px;
}

.checkout-form__actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 0.95rem;
  cursor: pointer;
  border: none;
}

.btn--primary {
  background: #2563eb;
  color: white;
}

.btn--primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn--outline {
  background: white;
  color: #374151;
  border: 1px solid #d1d5db;
}
</style>
