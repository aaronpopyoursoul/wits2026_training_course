import { computed } from 'vue'
import { useCartStore } from '@/store/cart'
import type { Product, ShippingAddress, Order } from '@/types'
import { useUserStore } from '@/store/user'

// ================================================================
// useCart — 購物車業務邏輯封裝
//
// 設計原則：
// - Composable 作為 View 與 Store 之間的橋樑，處理格式轉換與業務規則
// - 避免在 .vue 元件中直接操作 store，提高可測試性
// ================================================================

export function useCart() {
  const cartStore = useCartStore()
  const userStore = useUserStore()

  // --- 格式化後的計算屬性 ---

  /** 購物車商品清單（唯讀） */
  const cartItems = computed(() => cartStore.items)

  /** 商品總件數（顯示於購物車圖示 badge） */
  const totalCount = computed(() => cartStore.itemCount)

  /** 是否為空購物車 */
  const isEmpty = computed(() => cartStore.isEmpty)

  /** 格式化後的金額（含千位符號） */
  const formattedTotal = computed(() => {
    const { subtotal, shippingFee, total } = cartStore.calcTotal
    return {
      subtotal: formatPrice(subtotal),
      shippingFee: shippingFee === 0 ? '免運費' : formatPrice(shippingFee),
      total: formatPrice(total)
    }
  })

  /** 是否已達免運門檻 */
  const hasFreeShipping = computed(() =>
    cartStore.calcTotal.shippingFee === 0
  )

  // --- 操作方法 ---

  function addToCart(product: Product, quantity: number = 1): void {
    if (product.stock <= 0) {
      console.warn(`[Cart] 商品 ${product.name} 已無庫存`)
      return
    }
    cartStore.addItem(product, quantity)
  }

  function removeFromCart(productId: string): void {
    cartStore.removeItem(productId)
  }

  function updateItemQuantity(productId: string, quantity: number): void {
    cartStore.updateQuantity(productId, quantity)
  }

  function clearCart(): void {
    cartStore.clearCart()
  }

  async function submitOrder(shippingAddress: ShippingAddress): Promise<Order | null> {
    if (!userStore.isAuthenticated) {
      console.error('[Cart] 使用者尚未登入，無法結帳')
      return null
    }

    if (!userStore.currentUser) return null

    return await cartStore.checkout(userStore.currentUser.id, shippingAddress)
  }

  // --- 工具函式 ---

  /** 將分（整數）轉換為元的顯示格式，例如 49000 → "NT$ 490" */
  function formatPrice(amountInCent: number): string {
    return `NT$ ${(amountInCent / 100).toLocaleString('zh-TW')}`
  }

  return {
    cartItems,
    totalCount,
    isEmpty,
    formattedTotal,
    hasFreeShipping,
    addToCart,
    removeFromCart,
    updateItemQuantity,
    clearCart,
    submitOrder
  }
}
