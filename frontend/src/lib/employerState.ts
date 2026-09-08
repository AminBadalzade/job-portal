import type { MyCompanyResponse, Role, UserResponse } from '@/types'
import type { EmployerMembership } from '@/types'

export function deriveEmployerMembership(
  user: UserResponse | null | undefined,
): EmployerMembership | null {
  if (!user || user.role !== 'EMPLOYER') return null
  if (!user.company) return 'NONE'
  if (user.company.ownerId === user.id) return 'OWNER'
  return 'STAFF'
}

export function hasCompany(
  company: MyCompanyResponse | undefined,
): company is MyCompanyResponse {
  return company != null
}

export function roleHomePath(role: Role, membership: EmployerMembership | null) {
  if (role === 'JOB_SEEKER') return '/dashboard'
  if (role === 'EMPLOYER') {
    if (membership === 'NONE') return '/employer'
    return '/employer'
  }
  return '/jobs'
}
