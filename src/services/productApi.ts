@'
import type { Product, ApiResponse } from '@/types'

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'https://api.example.com'

export async function fetchProducts(): Promise<ApiResponse<Product[]>> {
  const res = await fetch(`${BASE_URL}/v1/products`)
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: 'ok' }
}

export async function fetchProductById(id: string): Promise<ApiResponse<Product>> {
  const res = await fetch(`${BASE_URL}/v1/products/${encodeURIComponent(id)}`)
  if (!res.ok) return { success: false, data: null, message: res.statusText }
  const data = await res.json()
  return { success: true, data, message: 'ok' }
}
'@ | Set-Content src\services\productApi.ts