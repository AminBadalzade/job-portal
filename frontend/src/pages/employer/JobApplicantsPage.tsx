import { applicationsApi } from '@/api/applications'
import { getApiError } from '@/api/client'
import { jobsApi } from '@/api/jobs'
import { resumesApi } from '@/api/resumes'
import { useAuth } from '@/auth/AuthContext'
import { Avatar } from '@/components/ui/Avatar'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { StatusPill } from '@/components/ui/StatusPill'
import { formatDate } from '@/lib/utils'
import type { ApplicationStatus, JobApplicationCompanyResponse } from '@/types'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Navigate, useParams } from 'react-router-dom'
import { toast } from 'sonner'

function nextActions(status: ApplicationStatus) {
  const actions: Array<{
    key: string
    label: string
    run: (id: number) => Promise<JobApplicationCompanyResponse>
  }> = []
  if (status === 'PENDING') {
    actions.push({
      key: 'review',
      label: 'Mark reviewed',
      run: applicationsApi.review,
    })
  }
  if (status === 'REVIEWED') {
    actions.push({
      key: 'interview',
      label: 'Move to interview',
      run: applicationsApi.interview,
    })
  }
  if (status === 'INTERVIEW') {
    actions.push({
      key: 'accept',
      label: 'Accept',
      run: applicationsApi.accept,
    })
  }
  if (status !== 'ACCEPTED' && status !== 'REJECTED') {
    actions.push({
      key: 'reject',
      label: 'Reject',
      run: applicationsApi.reject,
    })
  }
  return actions
}

export function JobApplicantsPage() {
  const { id } = useParams()
  const jobId = Number(id)
  const { membership } = useAuth()
  const qc = useQueryClient()
  const allowed = membership === 'OWNER' || membership === 'STAFF'

  const jobQuery = useQuery({
    queryKey: ['job', jobId],
    queryFn: () => jobsApi.getById(jobId),
    enabled: allowed && Number.isFinite(jobId),
  })

  // Uses /applications — not the dead /applicants stub
  const appsQuery = useQuery({
    queryKey: ['job-applications', jobId],
    queryFn: () => applicationsApi.forJob(jobId),
    enabled: allowed && Number.isFinite(jobId),
  })

  const transition = useMutation({
    mutationFn: ({
      id: appId,
      run,
    }: {
      id: number
      run: (id: number) => Promise<JobApplicationCompanyResponse>
    }) => run(appId),
    onSuccess: () => {
      toast.success('Application updated')
      void qc.invalidateQueries({ queryKey: ['job-applications', jobId] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  if (!allowed) return <Navigate to="/employer" replace />
  if (jobQuery.isLoading || appsQuery.isLoading) return <PageLoader />

  return (
    <div>
      <PageHeader
        title="Applicants"
        description={jobQuery.data?.title ?? 'Review applications for this role.'}
      />
      {appsQuery.isError ? (
        <EmptyState
          title="Could not load applicants"
          description={getApiError(appsQuery.error).message}
        />
      ) : null}
      {appsQuery.data?.length === 0 ? (
        <EmptyState title="No applications yet" />
      ) : null}
      {appsQuery.data && appsQuery.data.length > 0 ? (
        <Card className="overflow-hidden">
          {appsQuery.data.map((app) => (
            <div
              key={app.id}
              className="flex flex-col gap-3 border-b-[0.5px] border-border px-4 py-3.5 last:border-b-0 lg:flex-row lg:items-center lg:justify-between"
            >
              <div className="flex items-center gap-3">
                <Avatar
                  firstName={app.applicant.firstName}
                  lastName={app.applicant.lastName}
                />
                <div>
                  <p className="text-sm font-medium text-ink">
                    {app.applicant.firstName} {app.applicant.lastName}
                  </p>
                  <p className="text-xs text-ink-muted">
                    {app.applicant.email} · Applied {formatDate(app.appliedAt)}
                  </p>
                </div>
              </div>
              <div className="flex flex-wrap items-center gap-2">
                <StatusPill status={app.status} />
                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() =>
                    void resumesApi
                      .download(app.resume.id, app.resume.fileName)
                      .catch((err) => toast.error(getApiError(err).message))
                  }
                >
                  Resume
                </Button>
                {nextActions(app.status).map((action) => (
                  <Button
                    key={action.key}
                    size="sm"
                    variant={action.key === 'reject' ? 'secondary' : 'primary'}
                    disabled={transition.isPending}
                    onClick={() =>
                      transition.mutate({ id: app.id, run: action.run })
                    }
                  >
                    {action.label}
                  </Button>
                ))}
              </div>
            </div>
          ))}
        </Card>
      ) : null}
    </div>
  )
}
