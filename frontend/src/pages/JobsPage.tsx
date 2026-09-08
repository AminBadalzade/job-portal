import { getApiError } from '@/api/client'
import { jobsApi } from '@/api/jobs'
import { JobFilters } from '@/components/jobs/JobFilters'
import { JobListItem } from '@/components/jobs/JobListItem'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { Button } from '@/components/ui/Button'
import type { JobSearchRequest } from '@/types'
import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'

export function JobsPage() {
  const [filters, setFilters] = useState<JobSearchRequest>({})
  const [applied, setApplied] = useState<JobSearchRequest>({})
  const [page, setPage] = useState(0)

  const query = useQuery({
    queryKey: ['jobs', applied, page],
    queryFn: () =>
      jobsApi.search(applied, { pageNo: page, pageSize: 10 }),
  })

  return (
    <div>
      <PageHeader
        title="Find your next role"
        description="Browse openings across companies on Hearth."
      />

      <JobFilters
        value={filters}
        onChange={setFilters}
        onSearch={() => {
          setPage(0)
          setApplied(filters)
        }}
      />

      <div className="relative z-0 mt-6">
        {query.isLoading ? <PageLoader /> : null}
        {query.isError ? (
          <EmptyState
            title="Could not load jobs"
            description={getApiError(query.error).message}
          />
        ) : null}
        {query.data?.empty ? (
          <EmptyState
            title="No jobs match your filters"
            description="Try adjusting experience, location, or salary."
          />
        ) : null}
        {query.data && !query.data.empty ? (
          <>
            <Card className="overflow-hidden">
              {query.data.content.map((job) => (
                <JobListItem key={job.id} job={job} />
              ))}
            </Card>
            <div className="mt-4 flex items-center justify-between text-sm text-ink-secondary">
              <span>
                Page {query.data.number + 1} of {Math.max(query.data.totalPages, 1)}
              </span>
              <div className="flex gap-2">
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={query.data.first}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                >
                  Previous
                </Button>
                <Button
                  variant="secondary"
                  size="sm"
                  disabled={query.data.last}
                  onClick={() => setPage((p) => p + 1)}
                >
                  Next
                </Button>
              </div>
            </div>
          </>
        ) : null}
      </div>
    </div>
  )
}
