import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authApi } from '@/api/auth'
import { getApiError, tokenStorage } from '@/api/client'
import { deriveEmployerMembership, roleHomePath } from '@/lib/employerState'
import type {
  EmployerMembership,
  LoginRequest,
  RegisterRequest,
  UserResponse,
} from '@/types'

interface AuthContextValue {
  user: UserResponse | null
  loading: boolean
  membership: EmployerMembership | null
  isAuthenticated: boolean
  login: (body: LoginRequest) => Promise<UserResponse>
  register: (body: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  refreshUser: () => Promise<UserResponse | null>
  homePath: string
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null)
  const [loading, setLoading] = useState(true)

  const refreshUser = useCallback(async () => {
    if (!tokenStorage.getAccess() && !tokenStorage.getRefresh()) {
      setUser(null)
      return null
    }
    try {
      const me = await authApi.me()
      setUser(me)
      return me
    } catch {
      tokenStorage.clear()
      setUser(null)
      return null
    }
  }, [])

  useEffect(() => {
    void (async () => {
      await refreshUser()
      setLoading(false)
    })()
  }, [refreshUser])

  const login = useCallback(async (body: LoginRequest) => {
    const tokens = await authApi.login(body)
    tokenStorage.setTokens(tokens.accessToken, tokens.refreshToken)
    const me = await authApi.me()
    setUser(me)
    return me
  }, [])

  const register = useCallback(async (body: RegisterRequest) => {
    await authApi.register(body)
  }, [])

  const logout = useCallback(async () => {
    const refresh = tokenStorage.getRefresh()
    try {
      if (refresh) await authApi.logout(refresh)
    } catch {
      // still clear local session
    } finally {
      tokenStorage.clear()
      setUser(null)
    }
  }, [])

  const membership = useMemo(() => deriveEmployerMembership(user), [user])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      loading,
      membership,
      isAuthenticated: !!user,
      login,
      register,
      logout,
      refreshUser,
      homePath: user
        ? roleHomePath(user.role, membership)
        : '/jobs',
    }),
    [user, loading, membership, login, register, logout, refreshUser],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

export function useAuthErrorMessage(error: unknown) {
  return getApiError(error).message
}
