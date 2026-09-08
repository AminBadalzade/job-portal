import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { StatusPill } from '@/components/ui/StatusPill'
import { formatDate } from '@/lib/utils'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Navigate } from 'react-router-dom'
import { toast } from 'sonner'

export function JoinRequestsInboxPage() {
  const { user, membership } = useAuth()
  const qc = useQueryClient()
  const companyId = user?.company?.id
  const allowed = membership === 'OWNER' && !!companyId

  const query = useQuery({
    queryKey: ['join-requests', companyId],
    queryFn: () => companiesApi.listJoinRequests(companyId!),
    enabled: allowed,
  })

  const approve = useMutation({
    mutationFn: (requestId: number) =>
      companiesApi.approveJoinRequest(companyId!, requestId),
    onSuccess: () => {
      toast.success('Request approved')
      void qc.invalidateQueries({ queryKey: ['join-requests', companyId] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  const reject = useMutation({
    mutationFn: (requestId: number) =>
      companiesApi.rejectJoinRequest(companyId!, requestId),
    onSuccess: () => {
      toast.success('Request rejected')
      void qc.invalidateQueries({ queryKey: ['join-requests', companyId] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  if (!allowed) return <Navigate to="/employer" replace />

  return (
    <div>
      <PageHeader
        title="Join requests"
        description="Approve or reject employers who want to join your company."
      />
      {query.isLoading ? <PageLoader /> : null}
      {query.isError ? (
        <EmptyState
          title="Could not load join requests"
          description={getApiError(query.error).message}
        />
      ) : null}
      {query.data?.length === 0 ? (
        <EmptyState title="No join requests" />
      ) : null}
      {query.data && query.data.length > 0 ? (
        <Card className="overflow-hidden">
          {query.data.map((req) => (
            <div
              key={req.id}
              className="flex flex-col gap-3 border-b-[0.5px] border-border px-4 py-3 last:border-b-0 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="text-sm font-medium text-ink">
                  {req.firstName} {req.lastName}
                </p>
                <p className="text-xs text-ink-muted">
                  {formatDate(req.createdAt)}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <StatusPill status={req.status} kind="join" />
                {req.status === 'PENDING' ? (
                  <>
                    <Button
                      size="sm"
                      onClick={() => approve.mutate(req.id)}
                      disabled={approve.isPending || reject.isPending}
                    >
                      Approve
                    </Button>
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => reject.mutate(req.id)}
                      disabled={approve.isPending || reject.isPending}
                    >
                      Reject
                    </Button>
                  </>
                ) : null}
              </div>
            </div>
          ))}
        </Card>
      ) : null}
    </div>
  )
}
