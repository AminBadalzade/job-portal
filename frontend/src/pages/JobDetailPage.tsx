import { applicationsApi } from '@/api/applications'
import { getApiError } from '@/api/client'
import { jobsApi } from '@/api/jobs'
import { resumesApi } from '@/api/resumes'
import { useAuth } from '@/auth/AuthContext'
import { ApplyModal } from '@/components/applications/ApplyModal'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageLoader } from '@/components/ui/Spinner'
import { formatDate, formatSalary, labelize } from '@/lib/utils'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { toast } from 'sonner'

export function JobDetailPage() {
  const { id } = useParams()
  const jobId = Number(id)
  const { user } = useAuth()
  const [applyOpen, setApplyOpen] = useState(false)
  const qc = useQueryClient()

  const jobQuery = useQuery({
    queryKey: ['job', jobId],
    queryFn: () => jobsApi.getById(jobId),
    enabled: Number.isFinite(jobId),
  })

  const resumesQuery = useQuery({
    queryKey: ['resumes'],
    queryFn: () => resumesApi.list(),
    enabled: user?.role === 'JOB_SEEKER' && applyOpen,
  })

  const applyMutation = useMutation({
    mutationFn: (resumeId: number) =>
      applicationsApi.apply(jobId, { resumeId }),
    onSuccess: () => {
      toast.success('Application submitted')
      setApplyOpen(false)
      void qc.invalidateQueries({ queryKey: ['my-applications'] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  if (jobQuery.isLoading) return <PageLoader />
  if (jobQuery.isError || !jobQuery.data) {
    return (
      <EmptyState
        title="Job not found"
        description={getApiError(jobQuery.error).message}
        actionLabel="Back to jobs"
        onAction={() => history.back()}
      />
    )
  }

  const job = jobQuery.data
  const location = [job.city, job.country].filter(Boolean).join(', ')

  return (
    <div className="mx-auto max-w-3xl">
      <p className="text-sm text-ink-muted">
        <Link to="/jobs" className="text-accent">
          Jobs
        </Link>{' '}
        / {job.title}
      </p>
      <h1 className="mt-3 font-serif text-[32px] text-ink">{job.title}</h1>
      <p className="mt-1 text-ink-secondary">
        <Link to={`/companies/${job.company.id}`} className="text-accent">
          {job.company.name}
        </Link>
        {location ? ` · ${location}` : ''}
      </p>

      <div className="mt-4 flex flex-wrap gap-2 text-xs text-ink-secondary">
        <span className="rounded-[20px] bg-avatar px-2.5 py-1">
          {labelize(job.workType)}
        </span>
        <span className="rounded-[20px] bg-avatar px-2.5 py-1">
          {labelize(job.employmentType)}
        </span>
        <span className="rounded-[20px] bg-avatar px-2.5 py-1">
          {labelize(job.experienceLevel)}
        </span>
        <span className="rounded-[20px] bg-avatar px-2.5 py-1">
          {formatSalary(job.salaryMin, job.salaryMax)}
        </span>
      </div>

      {user?.role === 'JOB_SEEKER' ? (
        <Button className="mt-6" onClick={() => setApplyOpen(true)}>
          Apply with resume
        </Button>
      ) : !user ? (
        <Link
          to="/login"
          className="mt-6 inline-flex items-center justify-center rounded-[8px] bg-ink px-4 py-2.5 text-sm font-medium text-page"
        >
          Log in to apply
        </Link>
      ) : null}

      <Card className="mt-8 space-y-6 p-5">
        <section>
          <h2 className="font-serif text-xl">About the role</h2>
          <p className="mt-2 whitespace-pre-wrap text-sm text-ink-secondary">
            {job.description}
          </p>
        </section>
        {job.requirements ? (
          <section>
            <h2 className="font-serif text-xl">Requirements</h2>
            <p className="mt-2 whitespace-pre-wrap text-sm text-ink-secondary">
              {job.requirements}
            </p>
          </section>
        ) : null}
        {job.benefits ? (
          <section>
            <h2 className="font-serif text-xl">Benefits</h2>
            <p className="mt-2 whitespace-pre-wrap text-sm text-ink-secondary">
              {job.benefits}
            </p>
          </section>
        ) : null}
        <p className="text-xs text-ink-muted">
          Posted {formatDate(job.createdAt)}
          {job.expiresAt ? ` · Expires ${formatDate(job.expiresAt)}` : ''}
        </p>
      </Card>

      <ApplyModal
        open={applyOpen}
        resumes={resumesQuery.data ?? []}
        loading={resumesQuery.isLoading || applyMutation.isPending}
        onClose={() => setApplyOpen(false)}
        onApply={(resumeId) => applyMutation.mutate(resumeId)}
      />
    </div>
  )
}
