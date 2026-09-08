import { getApiError } from '@/api/client'
import { joinRequestsApi } from '@/api/joinRequests'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { StatusPill } from '@/components/ui/StatusPill'
import { formatDate } from '@/lib/utils'
import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'

export function MyJoinRequestsPage() {
  const query = useQuery({
    queryKey: ['my-join-requests'],
    queryFn: () => joinRequestsApi.mine(),
  })

  return (
    <div>
      <PageHeader
        title="My join requests"
        description="Backend currently returns the latest 5 requests only."
      />
      {query.isLoading ? <PageLoader /> : null}
      {query.isError ? (
        <EmptyState
          title="Could not load requests"
          description={getApiError(query.error).message}
        />
      ) : null}
      {query.data?.length === 0 ? (
        <EmptyState
          title="No join requests yet"
          description="Search companies and request to join."
          actionLabel="Search companies"
          onAction={() => {
            window.location.href = '/employer/companies'
          }}
        />
      ) : null}
      {query.data && query.data.length > 0 ? (
        <Card className="overflow-hidden">
          {query.data.map((req) => (
            <div
              key={req.id}
              className="flex items-center justify-between border-b-[0.5px] border-border px-4 py-3.5 last:border-b-0"
            >
              <div>
                <Link
                  to={`/companies/${req.companyId}`}
                  className="text-sm font-medium hover:text-accent"
                >
                  {req.companyName}
                </Link>
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
