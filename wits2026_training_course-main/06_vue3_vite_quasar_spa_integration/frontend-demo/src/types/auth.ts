export interface LoginForm {
  username: string
  password: string
}

export interface LoginResponse {
  tokenType: string
  accessToken: string
  expiresInSeconds: number
}

export interface ApiErrorResponse {
  errorCode: string
  message: string
  path: string
}