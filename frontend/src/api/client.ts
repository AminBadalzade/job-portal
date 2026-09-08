import axios, { type AxiosError, isAxiosError } from 'axios'
import type { ErrorResponse } from '@/types'

const ACCESS_KEY = 'hearth_access_token'
const REFRESH_KEY = 'hearth_refresh_token'

export const tokenStorage = {
  getAccess() {
    return localStorage.getItem(ACCESS_KEY)
  },
  getRefresh() {
    return localStorage.getItem(REFRESH_KEY)
  },
  setTokens(accessToken: string, refreshToken: string) {
    localStorage.setItem(ACCESS_KEY, accessToken)
    localStorage.setItem(REFRESH_KEY, refreshToken)
  },
  clear() {
    localStorage.removeItem(ACCESS_KEY)
    localStorage.removeItem(REFRESH_KEY)
  },
}

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const token = tokenStorage.getAccess()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let refreshPromise: Promise<string | null> | null = null

async function refreshAccessToken(): Promise<string | null> {
  const refreshToken = tokenStorage.getRefresh()
  if (!refreshToken) return null

  try {
    // No bearer header — refresh authenticates via body only
    const { data } = await axios.post<{
      accessToken: string
      refreshToken: string
    }>(
      `${import.meta.env.VITE_API_BASE_URL || ''}/api/refresh`,
      { refreshToken },
      { headers: { 'Content-Type': 'application/json' } },
    )
    tokenStorage.setTokens(data.accessToken, data.refreshToken)
    return data.accessToken
  } catch {
    tokenStorage.clear()
    return null
  }
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const original = error.config as typeof error.config & {
      _retry?: boolean
    }

    if (
      error.response?.status === 401 &&
      original &&
      !original._retry &&
      !original.url?.includes('/api/login') &&
      !original.url?.includes('/api/refresh') &&
      !original.url?.includes('/api/register')
    ) {
      original._retry = true
      refreshPromise ??= refreshAccessToken().finally(() => {
        refreshPromise = null
      })
      const newToken = await refreshPromise
      if (newToken) {
        original.headers = original.headers ?? {}
        original.headers.Authorization = `Bearer ${newToken}`
        return api(original)
      }
    }

    return Promise.reject(error)
  },
)

export function getApiError(error: unknown): ErrorResponse {
  if (isAxiosError(error) && error.response?.data) {
    const data = error.response.data as Partial<ErrorResponse>
    return {
      message: data.message || error.message || 'Request failed',
      status: data.status ?? error.response.status,
      timestamp: data.timestamp ?? new Date().toISOString(),
      path: data.path ?? '',
      errors: data.errors ?? null,
    }
  }
  return {
    message: error instanceof Error ? error.message : 'Something went wrong',
    status: 0,
    timestamp: new Date().toISOString(),
    path: '',
    errors: null,
  }
}
