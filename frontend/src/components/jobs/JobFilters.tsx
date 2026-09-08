import { Button } from '@/components/ui/Button'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { Select } from '@/components/ui/Select'
import {
  EMPLOYMENT_TYPES,
  EXPERIENCE_LEVELS,
  WORK_TYPES,
} from '@/constants/jobEnums'
import { cn, labelize } from '@/lib/utils'
import type {
  EmploymentType,
  ExperienceLevel,
  JobSearchRequest,
  WorkType,
} from '@/types'
import { useEffect, useRef, useState, type ReactNode } from 'react'

function FilterPopover({
  label,
  active,
  children,
}: {
  label: string
  active?: boolean
  children: ReactNode
}) {
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function onClick(e: MouseEvent) {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', onClick)
    return () => document.removeEventListener('mousedown', onClick)
  }, [])

  return (
    <div className="relative shrink-0" ref={ref}>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        aria-expanded={open}
        className={cn(
          'rounded-[20px] px-3 py-1.5 text-sm',
          active || open
            ? 'bg-accent-soft text-accent-ink'
            : 'bg-avatar text-ink-secondary hover:text-ink',
        )}
      >
        {label}
      </button>
      {open ? (
        <div
          className="absolute left-0 top-full z-50 mt-2 min-w-[240px] rounded-[12px] border-[0.5px] border-border bg-surface p-3 shadow-[0_12px_32px_rgba(44,44,42,0.14)]"
          onMouseDown={(e) => e.stopPropagation()}
        >
          {children}
          <Button
            size="sm"
            className="mt-3 w-full"
            onClick={() => setOpen(false)}
          >
            Done
          </Button>
        </div>
      ) : null}
    </div>
  )
}

export function JobFilters({
  value,
  onChange,
  onSearch,
}: {
  value: JobSearchRequest
  onChange: (next: JobSearchRequest) => void
  onSearch: () => void
}) {
  return (
    <div className="relative z-20 space-y-3">
      <div className="flex flex-col gap-2 sm:flex-row">
        <div className="min-w-0 flex-1">
          <Label htmlFor="job-search-title" className="sr-only">
            Job title
          </Label>
          <Input
            id="job-search-title"
            name="title"
            placeholder="Search by title"
            value={value.title ?? ''}
            onChange={(e) =>
              onChange({ ...value, title: e.target.value || undefined })
            }
            onKeyDown={(e) => {
              if (e.key === 'Enter') onSearch()
            }}
          />
        </div>
        <Button onClick={onSearch} className="sm:w-auto">
          Search
        </Button>
      </div>

      {/* flex-wrap (not overflow-x-auto) so absolute popovers are not clipped */}
      <div className="relative z-20 flex flex-wrap gap-2 pb-1">
        <FilterPopover label="Experience" active={!!value.experienceLevel}>
          <Label htmlFor="filter-experienceLevel">Experience level</Label>
          <Select
            id="filter-experienceLevel"
            name="experienceLevel"
            value={value.experienceLevel ?? ''}
            onChange={(e) =>
              onChange({
                ...value,
                experienceLevel: (e.target.value || undefined) as
                  | ExperienceLevel
                  | undefined,
              })
            }
          >
            <option value="">Any</option>
            {EXPERIENCE_LEVELS.map((v) => (
              <option key={v} value={v}>
                {labelize(v)}
              </option>
            ))}
          </Select>
        </FilterPopover>

        <FilterPopover label="Work type" active={!!value.workType}>
          <Label htmlFor="filter-workType">Work type</Label>
          <Select
            id="filter-workType"
            name="workType"
            value={value.workType ?? ''}
            onChange={(e) =>
              onChange({
                ...value,
                workType: (e.target.value || undefined) as WorkType | undefined,
              })
            }
          >
            <option value="">Any</option>
            {WORK_TYPES.map((v) => (
              <option key={v} value={v}>
                {labelize(v)}
              </option>
            ))}
          </Select>
        </FilterPopover>

        <FilterPopover label="Employment" active={!!value.employmentType}>
          <Label htmlFor="filter-employmentType">Employment type</Label>
          <Select
            id="filter-employmentType"
            name="employmentType"
            value={value.employmentType ?? ''}
            onChange={(e) =>
              onChange({
                ...value,
                employmentType: (e.target.value || undefined) as
                  | EmploymentType
                  | undefined,
              })
            }
          >
            <option value="">Any</option>
            {EMPLOYMENT_TYPES.map((v) => (
              <option key={v} value={v}>
                {labelize(v)}
              </option>
            ))}
          </Select>
        </FilterPopover>

        <FilterPopover
          label="Location"
          active={!!(value.city || value.country)}
        >
          <Label htmlFor="filter-city">City</Label>
          <Input
            id="filter-city"
            name="city"
            value={value.city ?? ''}
            onChange={(e) =>
              onChange({ ...value, city: e.target.value || undefined })
            }
          />
          <Label htmlFor="filter-country" className="mt-2">
            Country
          </Label>
          <Input
            id="filter-country"
            name="country"
            value={value.country ?? ''}
            onChange={(e) =>
              onChange({ ...value, country: e.target.value || undefined })
            }
          />
        </FilterPopover>

        <FilterPopover
          label="Salary"
          active={value.salaryMin != null || value.salaryMax != null}
        >
          <Label htmlFor="filter-salaryMin">Minimum</Label>
          <Input
            id="filter-salaryMin"
            name="salaryMin"
            type="number"
            min={0}
            value={value.salaryMin ?? ''}
            onChange={(e) =>
              onChange({
                ...value,
                salaryMin: e.target.value ? Number(e.target.value) : undefined,
              })
            }
          />
          <Label htmlFor="filter-salaryMax" className="mt-2">
            Maximum
          </Label>
          <Input
            id="filter-salaryMax"
            name="salaryMax"
            type="number"
            min={0}
            value={value.salaryMax ?? ''}
            onChange={(e) =>
              onChange({
                ...value,
                salaryMax: e.target.value ? Number(e.target.value) : undefined,
              })
            }
          />
        </FilterPopover>

        <button
          type="button"
          className="shrink-0 rounded-[20px] px-3 py-1.5 text-sm text-ink-muted hover:text-ink"
          onClick={() => onChange({})}
        >
          Clear
        </button>
      </div>
    </div>
  )
}
