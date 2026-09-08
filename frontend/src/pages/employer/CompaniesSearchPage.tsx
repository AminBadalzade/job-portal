import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { Avatar } from '@/components/ui/Avatar'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { EmptyState } from '@/components/ui/EmptyState'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { Link } from 'react-router-dom'
import { toast } from 'sonner'

export function CompaniesSearchPage() {
  const [name, setName] = useState('')
  const [queryName, setQueryName] = useState('')
  const qc = useQueryClient()

  const query = useQuery({
    queryKey: ['companies', queryName],
    queryFn: () => companiesApi.list(queryName || undefined),
  })

  const join = useMutation({
    mutationFn: (companyId: number) => companiesApi.join(companyId),
    onSuccess: () => {
      toast.success('Join request sent')
      void qc.invalidateQueries({ queryKey: ['my-join-requests'] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  return (
    <div>
      <PageHeader
        title="Find a company"
        description="Search and request to join as staff. Owners approve requests."
      />
      <div className="flex gap-2">
        <div className="min-w-0 flex-1">
          <Label htmlFor="company-search-name" className="sr-only">
            Company name
          </Label>
          <Input
            id="company-search-name"
            name="companyName"
            placeholder="Search by name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') setQueryName(name)
            }}
          />
        </div>
        <Button onClick={() => setQueryName(name)}>Search</Button>
      </div>

      <div className="mt-6">
        {query.isLoading ? <PageLoader /> : null}
        {query.isError ? (
          <EmptyState
            title="Could not load companies"
            description={getApiError(query.error).message}
          />
        ) : null}
        {query.data?.length === 0 ? (
          <EmptyState title="No companies found" />
        ) : null}
        {query.data && query.data.length > 0 ? (
          <Card className="overflow-hidden">
            {query.data.map((company) => (
              <div
                key={company.id}
                className="flex flex-col gap-3 border-b-[0.5px] border-border px-4 py-3.5 last:border-b-0 sm:flex-row sm:items-center sm:justify-between"
              >
                <div className="flex items-center gap-3">
                  <Avatar name={company.name} />
                  <div>
                    <Link
                      to={`/companies/${company.id}`}
                      className="text-sm font-medium hover:text-accent"
                    >
                      {company.name}
                    </Link>
                    <p className="text-xs text-ink-muted">
                      {company.industry ?? 'Industry not listed'}
                    </p>
                  </div>
                </div>
                <Button
                  size="sm"
                  disabled={join.isPending}
                  onClick={() => join.mutate(company.id)}
                >
                  Request to join
                </Button>
              </div>
            ))}
          </Card>
        ) : null}
      </div>
    </div>
  )
}
