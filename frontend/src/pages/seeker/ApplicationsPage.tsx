import { applicationsApi } from '@/api/applications'
import { getApiError } from '@/api/client'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { ConfirmDialog } from '@/components/ui/ConfirmDialog'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { StatusPill } from '@/components/ui/StatusPill'
import { formatDate } from '@/lib/utils'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { toast } from 'sonner'

export function ApplicationsPage() {
  const qc = useQueryClient()
  const [withdrawId, setWithdrawId] = useState<number | null>(null)

  const query = useQuery({
    queryKey: ['my-applications'],
    queryFn: () => applicationsApi.mine(),
  })

  const withdraw = useMutation({
    mutationFn: (id: number) => applicationsApi.withdraw(id),
    onSuccess: () => {
      toast.success('Application withdrawn')
      setWithdrawId(null)
      void qc.invalidateQueries({ queryKey: ['my-applications'] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  return (
    <div>
      <PageHeader
        title="My applications"
        description="Track every stage from pending through interview and decision."
      />

      {query.isLoading ? <PageLoader /> : null}
      {query.isError ? (
        <EmptyState
          title="Could not load applications"
          description={getApiError(query.error).message}
        />
      ) : null}
      {query.data?.length === 0 ? (
        <EmptyState
          title="No applications yet"
          description="Apply to a role using a resume from your library."
          actionLabel="Browse jobs"
          onAction={() => {
            window.location.href = '/jobs'
          }}
        />
      ) : null}

      {query.data && query.data.length > 0 ? (
        <Card className="overflow-hidden">
          {query.data.map((app) => (
            <div
              key={app.id}
              className="flex flex-col gap-3 border-b-[0.5px] border-border px-4 py-3.5 last:border-b-0 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <Link
                  to={`/jobs/${app.job.id}`}
                  className="text-sm font-medium text-ink hover:text-accent"
                >
                  {app.job.title}
                </Link>
                <p className="text-xs text-ink-muted">
                  {app.job.companyName} · Applied {formatDate(app.appliedAt)} ·
                  Resume {app.resume.fileName}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <StatusPill status={app.status} />
                {app.status !== 'ACCEPTED' && app.status !== 'REJECTED' ? (
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => setWithdrawId(app.id)}
                  >
                    Withdraw
                  </Button>
                ) : null}
              </div>
            </div>
          ))}
        </Card>
      ) : null}

      <ConfirmDialog
        open={withdrawId != null}
        title="Withdraw application?"
        description="This removes your application for this job. You can apply again later if the posting is still open."
        confirmLabel="Withdraw"
        danger
        loading={withdraw.isPending}
        onCancel={() => setWithdrawId(null)}
        onConfirm={() => withdrawId && withdraw.mutate(withdrawId)}
      />
    </div>
  )
}
