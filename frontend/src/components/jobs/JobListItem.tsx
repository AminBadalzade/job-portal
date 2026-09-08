import { Link } from 'react-router-dom'
import { formatDate, formatSalary, labelize } from '@/lib/utils'
import type { JobSummaryResponse } from '@/types'

export function JobListItem({ job }: { job: JobSummaryResponse }) {
  const location = [job.city, job.country].filter(Boolean).join(', ')

  return (
    <Link
      to={`/jobs/${job.id}`}
      className="block border-b-[0.5px] border-border px-4 py-3.5 transition-colors last:border-b-0 hover:bg-page"
    >
      <div className="flex flex-col gap-1 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h3 className="text-base font-medium text-ink">{job.title}</h3>
          <p className="text-sm text-ink-secondary">{job.companyName}</p>
        </div>
        <p className="text-xs text-ink-muted">{formatDate(job.createdAt)}</p>
      </div>
      <div className="mt-2 flex flex-wrap gap-2 text-xs text-ink-secondary">
        {location ? <span>{location}</span> : null}
        <span>{labelize(job.workType)}</span>
        <span>{labelize(job.employmentType)}</span>
        <span>{labelize(job.experienceLevel)}</span>
        <span>{formatSalary(job.salaryMin, job.salaryMax)}</span>
      </div>
    </Link>
  )
}
