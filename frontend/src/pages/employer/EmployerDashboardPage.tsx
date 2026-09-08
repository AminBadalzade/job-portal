import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { joinRequestsApi } from '@/api/joinRequests'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { StatusPill } from '@/components/ui/StatusPill'
import { formatDate } from '@/lib/utils'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'

export function EmployerDashboardPage() {
  const { user, membership } = useAuth()

  const myRequests = useQuery({
    queryKey: ['my-join-requests'],
    queryFn: () => joinRequestsApi.mine(),
    enabled: membership === 'NONE',
  })

  const companyJobs = useQuery({
    queryKey: ['company-jobs', user?.company?.id],
    queryFn: () => companiesApi.getJobs(user!.company!.id),
    enabled: !!user?.company?.id,
  })

  const pendingJoin = myRequests.data?.some((r) => r.status === 'PENDING')

  if (membership === 'NONE') {
    return (
      <div>
        <PageHeader
          title={`Welcome, ${user?.firstName ?? ''}`}
          description="Create a company or request to join an existing one."
        />

        {pendingJoin ? (
          <Card className="mb-6 border-accent/30 bg-accent-soft/40 p-4">
            <p className="text-sm font-medium text-accent-ink">
              You have a pending join request
            </p>
            <p className="mt-1 text-sm text-ink-secondary">
              You are not staff until an owner approves. You can still create a
              company or request to join others.
            </p>
          </Card>
        ) : null}

        <div className="grid gap-4 md:grid-cols-2">
          <Card className="p-5">
            <h2 className="font-serif text-2xl">Create a company</h2>
            <p className="mt-2 text-sm text-ink-secondary">
              Become the owner. Post jobs, review applicants, and approve staff
              join requests.
            </p>
            <Button className="mt-4" as-child={undefined}>
              <Link
                to="/employer/create-company"
                className="inline-flex rounded-[8px] bg-ink px-4 py-2.5 text-sm font-medium text-page"
              >
                Create company
              </Link>
            </Button>
          </Card>
          <Card className="p-5">
            <h2 className="font-serif text-2xl">Join a company</h2>
            <p className="mt-2 text-sm text-ink-secondary">
              Search existing companies and request to join as staff.
            </p>
            <div className="mt-4 flex flex-wrap gap-2">
              <Link
                to="/employer/companies"
                className="inline-flex rounded-[8px] border-[0.5px] border-border px-4 py-2.5 text-sm font-medium"
              >
                Search companies
              </Link>
              <Link
                to="/employer/my-join-requests"
                className="inline-flex rounded-[8px] border-[0.5px] border-border px-4 py-2.5 text-sm font-medium"
              >
                My join requests
              </Link>
            </div>
          </Card>
        </div>

        {myRequests.isLoading ? <PageLoader /> : null}
        {myRequests.data && myRequests.data.length > 0 ? (
          <Card className="mt-6 overflow-hidden">
            {myRequests.data.map((req) => (
              <div
                key={req.id}
                className="flex items-center justify-between border-b-[0.5px] border-border px-4 py-3 last:border-b-0"
              >
                <div>
                  <p className="text-sm font-medium">{req.companyName}</p>
                  <p className="text-xs text-ink-muted">
                    {formatDate(req.createdAt)}
                  </p>
                </div>
                <StatusPill status={req.status} kind="join" />
              </div>
            ))}
          </Card>
        ) : null}
      </div>
    )
  }

  return (
    <div>
      <PageHeader
        title={user?.company?.name ?? 'Employer dashboard'}
        description={
          membership === 'OWNER'
            ? 'You own this company. Manage jobs, applicants, and join requests.'
            : 'You are staff. Manage jobs and review applicants for your company.'
        }
        actions={
          <Link
            to="/employer/jobs/new"
            className="inline-flex rounded-[8px] bg-ink px-4 py-2.5 text-sm font-medium text-page"
          >
            Post a job
          </Link>
        }
      />

      <div className="grid gap-3 sm:grid-cols-3">
        <Card className="p-4">
          <p className="text-xs text-ink-muted">Open jobs</p>
          <p className="mt-1 text-2xl font-medium">
            {companyJobs.data?.length ?? '—'}
          </p>
        </Card>
        <Card className="p-4">
          <p className="text-xs text-ink-muted">Your role</p>
          <p className="mt-1 text-2xl font-medium">
            {membership === 'OWNER' ? 'Owner' : 'Staff'}
          </p>
        </Card>
        <Card className="p-4">
          <p className="text-xs text-ink-muted">Industry</p>
          <p className="mt-1 text-lg font-medium">
            {user?.company?.industry ?? '—'}
          </p>
        </Card>
      </div>

      <h2 className="mt-8 font-serif text-2xl">Company jobs</h2>
      {companyJobs.isLoading ? <PageLoader /> : null}
      {companyJobs.isError ? (
        <EmptyState
          title="Could not load jobs"
          description={getApiError(companyJobs.error).message}
        />
      ) : null}
      {companyJobs.data?.length === 0 ? (
        <EmptyState
          title="No jobs yet"
          description="Post your first opening."
          actionLabel="Post a job"
          onAction={() => {
            window.location.href = '/employer/jobs/new'
          }}
        />
      ) : null}
      {companyJobs.data && companyJobs.data.length > 0 ? (
        <Card className="mt-3 overflow-hidden">
          {companyJobs.data.map((job) => (
            <div
              key={job.id}
              className="flex flex-col gap-2 border-b-[0.5px] border-border px-4 py-3 last:border-b-0 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <Link
                  to={`/jobs/${job.id}`}
                  className="text-sm font-medium hover:text-accent"
                >
                  {job.title}
                </Link>
                <p className="text-xs text-ink-muted">
                  {formatDate(job.createdAt)}
                </p>
              </div>
              <div className="flex gap-2">
                <Link
                  to={`/employer/jobs/${job.id}/applicants`}
                  className="rounded-[8px] border-[0.5px] border-border px-3 py-1.5 text-sm"
                >
                  Applicants
                </Link>
                <Link
                  to={`/employer/jobs/${job.id}/edit`}
                  className="rounded-[8px] border-[0.5px] border-border px-3 py-1.5 text-sm"
                >
                  Edit
                </Link>
              </div>
            </div>
          ))}
        </Card>
      ) : null}
    </div>
  )
}
