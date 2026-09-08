import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { JobListItem } from '@/components/jobs/JobListItem'
import { Avatar } from '@/components/ui/Avatar'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageLoader } from '@/components/ui/Spinner'
import { useQuery } from '@tanstack/react-query'
import { useParams } from 'react-router-dom'

export function CompanyPage() {
  const { id } = useParams()
  const companyId = Number(id)

  const companyQuery = useQuery({
    queryKey: ['company', companyId],
    queryFn: () => companiesApi.getById(companyId),
    enabled: Number.isFinite(companyId),
  })

  const jobsQuery = useQuery({
    queryKey: ['company-jobs', companyId],
    queryFn: () => companiesApi.getJobs(companyId),
    enabled: Number.isFinite(companyId),
  })

  if (companyQuery.isLoading) return <PageLoader />
  if (companyQuery.isError || !companyQuery.data) {
    return (
      <EmptyState
        title="Company not found"
        description={getApiError(companyQuery.error).message}
      />
    )
  }

  const company = companyQuery.data

  return (
    <div>
      <div className="flex items-start gap-4">
        <Avatar name={company.name} className="h-14 w-14 text-base" />
        <div>
          <h1 className="font-serif text-[32px] text-ink">{company.name}</h1>
          <p className="text-sm text-ink-secondary">{company.industry}</p>
        </div>
      </div>

      <Card className="mt-6 p-5">
        <h2 className="font-serif text-xl">About</h2>
        <p className="mt-2 whitespace-pre-wrap text-sm text-ink-secondary">
          {company.description}
        </p>
      </Card>

      <h2 className="mt-8 font-serif text-2xl">Open roles</h2>
      <div className="mt-3">
        {jobsQuery.isLoading ? <PageLoader /> : null}
        {jobsQuery.data?.length === 0 ? (
          <EmptyState title="No open jobs yet" />
        ) : null}
        {jobsQuery.data && jobsQuery.data.length > 0 ? (
          <Card className="overflow-hidden">
            {jobsQuery.data.map((job) => (
              <JobListItem key={job.id} job={job} />
            ))}
          </Card>
        ) : null}
      </div>
    </div>
  )
}
