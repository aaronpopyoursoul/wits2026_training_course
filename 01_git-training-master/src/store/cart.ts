import { defineStore } from "pinia";
import { ref, computed } from "vue";
import type { CartItem, Product, Order, ShippingAddress } from "@/types";

// ================================================================
// cart store — 管理購物車狀態
//
// 【情境一熱修復點】
// calcTotal() 函式中有一個運算 Bug：
//   - 錯誤版本：subtotal 計算未乘以 quantity
//   - 修復版本：需修正為 item.product.price * item.quantity
//
// 學員需要在 hotfix/calc-error 分支上修復此問題，
// 再合併回 main，然後將修復同步到 feature/cart-logic。
// ================================================================

export const useCartStore = defineStore("cart", () => {
  // --- State ---
  const items = ref<CartItem[]>([]);
  const isCheckingOut = ref(false);

  // --- Getters ---
  const itemCount = computed(() =>
    items.value.reduce((total, item) => total + item.quantity, 0),
  );

  /**
   * 計算購物車金額
   *
   * ⚠️ BUG（情境一修復目標）：
   * 此處的 subtotal 計算忘記乘以 quantity，
   * 導致不管買幾個，小計都只算一件的價格。
   *
   * 錯誤行：item.product.price（應為 item.product.price * item.quantity）
   */
  const calcTotal = computed(() => {
    const subtotal = items.value.reduce(
      (sum, item) => sum + item.product.price * item.quantity, // ← BUG: 應為 * item.quantity
      0,
    );
    const shippingFee = subtotal >= 49000 ? 0 : 6000; // 單位：分；滿 490 元免運
    return {
      subtotal,
      shippingFee,
      total: subtotal + shippingFee,
    };
  });

  const isEmpty = computed(() => items.value.length === 0);

  // --- Actions ---

  /** 加入商品到購物車，若已存在則增加數量 */
  function addItem(product: Product, quantity: number = 1): void {
    const existing = items.value.find((item) => item.product.id === product.id);

    if (existing) {
      existing.quantity = Math.min(existing.quantity + quantity, product.stock);
    } else {
      items.value.push({
        product,
        quantity: Math.min(quantity, product.stock),
      });
    }
  }

  /** 移除購物車中的商品 */
  function removeItem(productId: string): void {
    const index = items.value.findIndex(
      (item) => item.product.id === productId,
    );
    if (index !== -1) {
      items.value.splice(index, 1);
    }
  }

  /** 更新商品數量；若數量為 0 則移除 */
  function updateQuantity(productId: string, quantity: number): void {
    if (quantity <= 0) {
      removeItem(productId);
      return;
    }

    const item = items.value.find((i) => i.product.id === productId);
    if (item) {
      item.quantity = Math.min(quantity, item.product.stock);
    }
  }

  /** 清空購物車 */
  function clearCart(): void {
    items.value = [];
  }

  /** 結帳：建立訂單（模擬 API 呼叫） */
  async function checkout(
    userId: string,
    shippingAddress: ShippingAddress,
  ): Promise<Order | null> {
    if (isEmpty.value) return null;

    isCheckingOut.value = true;

    try {
      const { subtotal, shippingFee, total } = calcTotal.value;

      const order: Order = {
        id: `ord_${Date.now()}`,
        userId,
        items: [...items.value],
        subtotal,
        shippingFee,
        total,
        status: "pending",
        shippingAddress,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };

      // 模擬 API 延遲
      await new Promise((resolve) => setTimeout(resolve, 800));

      clearCart();
      return order;
    } finally {
      isCheckingOut.value = false;
    }
  }

  return {
    items,
    isCheckingOut,
    itemCount,
    calcTotal,
    isEmpty,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    checkout,
  };
});
