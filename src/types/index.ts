// ============================================================
// 全域型別定義
// 集中在此處管理，避免跨模組型別不一致
// ============================================================

/** 使用者角色 */
export type UserRole = 'admin' | 'member' | 'guest'

/** 使用者基本資訊 */
export interface User {
  id: string
  username: string
  email: string
  role: UserRole
  avatarUrl?: string
  createdAt: string  // ISO 8601
}

/** 認證狀態（登入後 API 回傳） */
export interface AuthToken {
  accessToken: string
  refreshToken: string
  expiresIn: number  // seconds
}

/** 商品項目 */
export interface Product {
  id: string
  name: string
  description: string
  price: number       // 單位：元（整數，避免浮點數問題）
  stock: number
  imageUrl?: string
  categoryId: string
}

/** 購物車項目 */
export interface CartItem {
  product: Product
  quantity: number
}

/** 訂單狀態 */
export type OrderStatus =
  | 'pending'     // 待付款
  | 'paid'        // 已付款
  | 'shipped'     // 已出貨
  | 'delivered'   // 已送達
  | 'cancelled'   // 已取消

/** 訂單 */
export interface Order {
  id: string
  userId: string
  items: CartItem[]
  subtotal: number      // 商品小計（分）
  shippingFee: number   // 運費（分）
  total: number         // 實收金額（分）
  status: OrderStatus
  shippingAddress: ShippingAddress
  createdAt: string
  updatedAt: string
}

/** 收貨地址 */
export interface ShippingAddress {
  recipientName: string
  phone: string
  zipCode: string
  city: string
  district: string
  addressLine: string
}

/** API 統一回應格式 */
export interface ApiResponse<T> {
  success: boolean
  data: T | null
  message: string
  errorCode?: string
}
