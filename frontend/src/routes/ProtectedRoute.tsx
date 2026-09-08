import { useAuth } from '@/auth/AuthContext'
import { PageLoader } from '@/components/ui/Spinner'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import type { Role } from '@/types'
import type { EmployerMembership } from '@/types'

export function ProtectedRoute({
  roles,
  memberships,
}: {
  roles?: Role[]
  memberships?: EmployerMembership[]
}) {
  const { user, loading, membership, isAuthenticated } = useAuth()
  const location = useLocation()

  if (loading) return <PageLoader />

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  if (roles && !roles.includes(user.role)) {
    return <Navigate to={user.role === 'EMPLOYER' ? '/employer' : '/dashboard'} replace />
  }

  if (
    memberships &&
    user.role === 'EMPLOYER' &&
    (membership == null || !memberships.includes(membership))
  ) {
    return <Navigate to="/employer" replace />
  }

  return <Outlet />
}

export function GuestOnlyRoute() {
  const { isAuthenticated, loading, homePath } = useAuth()
  if (loading) return <PageLoader />
  if (isAuthenticated) return <Navigate to={homePath} replace />
  return <Outlet />
}
