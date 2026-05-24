import type { Order, ShippingAddress, ApiResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'https://api.example.com'

export async function createOrder(
  userId: string,
  shippingAddress: ShippingAddress
): Promise<ApiResponse<Order>> {
  const res = await fetch(`${BASE_URL}/v1/orders`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, shippingAddress })
  })
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: '訂單建立成功' }
}
