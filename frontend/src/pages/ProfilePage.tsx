import { useAuth } from '@/auth/AuthContext'
import { Card } from '@/components/ui/Card'
import { PageHeader } from '@/components/ui/PageHeader'
import { Avatar } from '@/components/ui/Avatar'

export function ProfilePage() {
  const { user, membership } = useAuth()
  if (!user) return null

  return (
    <div className="mx-auto max-w-lg">
      <PageHeader title="Profile" description="Your account details from Hearth." />
      <Card className="p-5">
        <div className="flex items-center gap-3">
          <Avatar
            firstName={user.firstName}
            lastName={user.lastName}
            className="h-12 w-12"
          />
          <div>
            <p className="font-medium text-ink">
              {user.firstName} {user.lastName}
            </p>
            <p className="text-sm text-ink-secondary">{user.email}</p>
          </div>
        </div>
        <dl className="mt-5 space-y-3 text-sm">
          <div className="flex justify-between border-b-[0.5px] border-border py-2">
            <dt className="text-ink-muted">Role</dt>
            <dd className="text-ink">
              {user.role === 'JOB_SEEKER'
                ? 'Job seeker'
                : user.role === 'EMPLOYER'
                  ? 'Employer'
                  : user.role}
            </dd>
          </div>
          {user.role === 'EMPLOYER' ? (
            <>
              <div className="flex justify-between border-b-[0.5px] border-border py-2">
                <dt className="text-ink-muted">Membership</dt>
                <dd className="text-ink">
                  {membership === 'OWNER'
                    ? 'Owner'
                    : membership === 'STAFF'
                      ? 'Staff'
                      : 'No company'}
                </dd>
              </div>
              {user.company ? (
                <div className="flex justify-between py-2">
                  <dt className="text-ink-muted">Company</dt>
                  <dd className="text-ink">{user.company.name}</dd>
                </div>
              ) : null}
            </>
          ) : null}
        </dl>
      </Card>
    </div>
  )
}
