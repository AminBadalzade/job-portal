import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { jobsApi } from '@/api/jobs'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { ConfirmDialog } from '@/components/ui/ConfirmDialog'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { formatDate } from '@/lib/utils'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { toast } from 'sonner'

export function MyJobsPage() {
  const { user, membership } = useAuth()
  const qc = useQueryClient()
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const companyId = user?.company?.id
  const allowed = (membership === 'OWNER' || membership === 'STAFF') && !!companyId

  const query = useQuery({
    queryKey: ['company-jobs', companyId],
    queryFn: () => companiesApi.getJobs(companyId!),
    enabled: allowed,
  })

  const remove = useMutation({
    mutationFn: (id: number) => jobsApi.remove(id),
    onSuccess: () => {
      toast.success('Job deleted')
      setDeleteId(null)
      void qc.invalidateQueries({ queryKey: ['company-jobs', companyId] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  if (!allowed) return <Navigate to="/employer" replace />

  return (
    <div>
      <PageHeader
        title="My jobs"
        description="Manage openings for your company."
        actions={
          <Link
            to="/employer/jobs/new"
            className="inline-flex rounded-[8px] bg-ink px-4 py-2.5 text-sm font-medium text-page"
          >
            Post a job
          </Link>
        }
      />
      {query.isLoading ? <PageLoader /> : null}
      {query.isError ? (
        <EmptyState
          title="Could not load jobs"
          description={getApiError(query.error).message}
        />
      ) : null}
      {query.data?.length === 0 ? (
        <EmptyState title="No jobs yet" description="Post your first opening." />
      ) : null}
      {query.data && query.data.length > 0 ? (
        <Card className="overflow-hidden">
          {query.data.map((job) => (
            <div
              key={job.id}
              className="flex flex-col gap-3 border-b-[0.5px] border-border px-4 py-3.5 last:border-b-0 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="text-sm font-medium">{job.title}</p>
                <p className="text-xs text-ink-muted">
                  {formatDate(job.createdAt)}
                </p>
              </div>
              <div className="flex flex-wrap gap-2">
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
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => setDeleteId(job.id)}
                >
                  Delete
                </Button>
              </div>
            </div>
          ))}
        </Card>
      ) : null}

      <ConfirmDialog
        open={deleteId != null}
        title="Delete job?"
        description="This permanently removes the job posting."
        confirmLabel="Delete"
        danger
        loading={remove.isPending}
        onCancel={() => setDeleteId(null)}
        onConfirm={() => deleteId && remove.mutate(deleteId)}
      />
    </div>
  )
}
