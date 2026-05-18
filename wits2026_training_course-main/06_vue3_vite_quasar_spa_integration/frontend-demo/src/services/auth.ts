import { http } from './http'
import type { LoginForm, LoginResponse } from '@/types/auth'

export async function loginRequest(payload: LoginForm) {
  const { data } = await http.post<LoginResponse>('/api/auth/login', payload)
  return data
}