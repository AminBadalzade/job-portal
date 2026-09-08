import { applicationsApi } from '@/api/applications'
import { getApiError } from '@/api/client'
import { jobsApi } from '@/api/jobs'
import { useAuth } from '@/auth/AuthContext'
import { JobListItem } from '@/components/jobs/JobListItem'
import { StatusPill } from '@/components/ui/StatusPill'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { formatDate } from '@/lib/utils'
import type { ApplicationStatus } from '@/types'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'

const STATUSES: ApplicationStatus[] = [
  'PENDING',
  'REVIEWED',
  'INTERVIEW',
  'ACCEPTED',
  'REJECTED',
]

export function SeekerDashboardPage() {
  const { user } = useAuth()

  const appsQuery = useQuery({
    queryKey: ['my-applications'],
    queryFn: () => applicationsApi.mine(),
  })

  const jobsQuery = useQuery({
    queryKey: ['jobs', 'dashboard'],
    queryFn: () => jobsApi.search({}, { pageNo: 0, pageSize: 5 }),
  })

  const counts = STATUSES.reduce(
    (acc, status) => {
      acc[status] =
        appsQuery.data?.filter((a) => a.status === status).length ?? 0
      return acc
    },
    {} as Record<ApplicationStatus, number>,
  )

  return (
    <div>
      <PageHeader
        title={`Hello, ${user?.firstName ?? ''}`}
        description="Track applications and discover new roles."
      />

      <div className="grid grid-cols-2 gap-2 sm:grid-cols-5">
        {STATUSES.map((status) => (
          <Card key={status} className="px-3 py-3">
            <StatusPill status={status} />
            <p className="mt-2 text-2xl font-medium text-ink">{counts[status]}</p>
          </Card>
        ))}
      </div>

      <div className="mt-8 grid gap-8 lg:grid-cols-2">
        <section>
          <div className="mb-3 flex items-center justify-between">
            <h2 className="font-serif text-2xl">Recent applications</h2>
            <Link to="/applications" className="text-sm text-accent">
              View all
            </Link>
          </div>
          {appsQuery.isLoading ? <PageLoader /> : null}
          {appsQuery.isError ? (
            <EmptyState
              title="Could not load applications"
              description={getApiError(appsQuery.error).message}
            />
          ) : null}
          {appsQuery.data?.length === 0 ? (
            <EmptyState
              title="No applications yet"
              description="Browse jobs and apply with a resume from your library."
              actionLabel="Browse jobs"
              onAction={() => {
                window.location.href = '/jobs'
              }}
            />
          ) : null}
          {appsQuery.data && appsQuery.data.length > 0 ? (
            <Card className="overflow-hidden">
              {appsQuery.data.slice(0, 5).map((app) => (
                <Link
                  key={app.id}
                  to="/applications"
                  className="flex items-center justify-between gap-3 border-b-[0.5px] border-border px-4 py-3 last:border-b-0 hover:bg-page"
                >
                  <div>
                    <p className="text-sm font-medium text-ink">
                      {app.job.title}
                    </p>
                    <p className="text-xs text-ink-muted">
                      {app.job.companyName} · {formatDate(app.appliedAt)}
                    </p>
                  </div>
                  <StatusPill status={app.status} />
                </Link>
              ))}
            </Card>
          ) : null}
        </section>

        <section>
          <div className="mb-3 flex items-center justify-between">
            <h2 className="font-serif text-2xl">Recent jobs</h2>
            <Link to="/jobs" className="text-sm text-accent">
              Browse
            </Link>
          </div>
          {jobsQuery.isLoading ? <PageLoader /> : null}
          {jobsQuery.data && !jobsQuery.data.empty ? (
            <Card className="overflow-hidden">
              {jobsQuery.data.content.map((job) => (
                <JobListItem key={job.id} job={job} />
              ))}
            </Card>
          ) : (
            <EmptyState title="No jobs available" />
          )}
        </section>
      </div>
    </div>
  )
}
