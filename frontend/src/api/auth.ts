import { api } from '@/api/client'
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserResponse,
} from '@/types'

export const authApi = {
  register(body: RegisterRequest) {
    return api.post<UserResponse>('/api/register', body).then((r) => r.data)
  },
  login(body: LoginRequest) {
    return api.post<AuthResponse>('/api/login', body).then((r) => r.data)
  },
  logout(refreshToken: string) {
    return api.post('/api/logout', { refreshToken })
  },
  me() {
    return api.get<UserResponse>('/api/me').then((r) => r.data)
  },
}
