import { getApiError } from '@/api/client'
import { jobsApi } from '@/api/jobs'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { PageHeader } from '@/components/ui/PageHeader'
import { Select } from '@/components/ui/Select'
import { Textarea } from '@/components/ui/Textarea'
import { FieldError } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import {
  EMPLOYMENT_TYPES,
  EXPERIENCE_LEVELS,
  WORK_TYPES,
} from '@/constants/jobEnums'
import { labelize } from '@/lib/utils'
import type {
  CreateJobRequest,
  EmploymentType,
  ExperienceLevel,
  WorkType,
} from '@/types'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import { Navigate, useNavigate, useParams } from 'react-router-dom'
import { toast } from 'sonner'

function defaultExpiresAt() {
  const d = new Date()
  d.setDate(d.getDate() + 30)
  return d.toISOString().slice(0, 16)
}

const emptyForm: CreateJobRequest = {
  title: '',
  description: '',
  experienceLevel: 'MID',
  workType: 'HYBRID',
  employmentType: 'FULL_TIME',
  city: '',
  country: '',
  requirements: '',
  benefits: '',
  expiresAt: defaultExpiresAt(),
}

export function JobFormPage() {
  const { id } = useParams()
  const isEdit = Boolean(id)
  const jobId = Number(id)
  const { membership } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState<CreateJobRequest>(emptyForm)
  const [error, setError] = useState('')
  const allowed = membership === 'OWNER' || membership === 'STAFF'

  const existing = useQuery({
    queryKey: ['job', jobId],
    queryFn: () => jobsApi.getById(jobId),
    enabled: allowed && isEdit && Number.isFinite(jobId),
  })

  useEffect(() => {
    if (existing.data) {
      const j = existing.data
      setForm({
        title: j.title,
        description: j.description,
        salaryMin: j.salaryMin ?? undefined,
        salaryMax: j.salaryMax ?? undefined,
        experienceLevel: j.experienceLevel,
        workType: j.workType,
        employmentType: j.employmentType,
        city: j.city ?? '',
        country: j.country ?? '',
        requirements: j.requirements ?? '',
        benefits: j.benefits ?? '',
        expiresAt: j.expiresAt
          ? j.expiresAt.slice(0, 16)
          : defaultExpiresAt(),
      })
    }
  }, [existing.data])

  const save = useMutation({
    mutationFn: async () => {
      const payload: CreateJobRequest = {
        ...form,
        city: form.city || undefined,
        country: form.country || undefined,
        requirements: form.requirements || undefined,
        benefits: form.benefits || undefined,
        // Backend Job.expiresAt is non-null — always send a value on create
        expiresAt: form.expiresAt
          ? new Date(form.expiresAt).toISOString().slice(0, 19)
          : undefined,
      }
      if (isEdit) return jobsApi.update(jobId, payload)
      return jobsApi.create(payload)
    },
    onSuccess: (job) => {
      toast.success(isEdit ? 'Job updated' : 'Job posted')
      navigate(`/employer/jobs/${job.id}/applicants`)
    },
    onError: (err) => setError(getApiError(err).message),
  })

  if (!allowed) return <Navigate to="/employer" replace />
  if (isEdit && existing.isLoading) return <PageLoader />

  return (
    <div className="mx-auto max-w-2xl">
      <PageHeader
        title={isEdit ? 'Edit job' : 'Post a job'}
        description="Company is set from your account — do not send companyId."
      />
      <Card className="p-5">
        <form
          className="space-y-4"
          onSubmit={(e) => {
            e.preventDefault()
            setError('')
            save.mutate()
          }}
        >
          <div>
            <Label htmlFor="title">Title</Label>
            <Input
              id="title"
              name="title"
              required
              value={form.title}
              onChange={(e) => setForm((f) => ({ ...f, title: e.target.value }))}
            />
          </div>
          <div>
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              name="description"
              required
              rows={5}
              value={form.description}
              onChange={(e) =>
                setForm((f) => ({ ...f, description: e.target.value }))
              }
            />
          </div>
          <div className="grid gap-3 sm:grid-cols-3">
            <div>
              <Label htmlFor="experienceLevel">Experience</Label>
              <Select
                id="experienceLevel"
                name="experienceLevel"
                value={form.experienceLevel}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    experienceLevel: e.target.value as ExperienceLevel,
                  }))
                }
              >
                {EXPERIENCE_LEVELS.map((v) => (
                  <option key={v} value={v}>
                    {labelize(v)}
                  </option>
                ))}
              </Select>
            </div>
            <div>
              <Label htmlFor="workType">Work type</Label>
              <Select
                id="workType"
                name="workType"
                value={form.workType}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    workType: e.target.value as WorkType,
                  }))
                }
              >
                {WORK_TYPES.map((v) => (
                  <option key={v} value={v}>
                    {labelize(v)}
                  </option>
                ))}
              </Select>
            </div>
            <div>
              <Label htmlFor="employmentType">Employment</Label>
              <Select
                id="employmentType"
                name="employmentType"
                value={form.employmentType}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    employmentType: e.target.value as EmploymentType,
                  }))
                }
              >
                {EMPLOYMENT_TYPES.map((v) => (
                  <option key={v} value={v}>
                    {labelize(v)}
                  </option>
                ))}
              </Select>
            </div>
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <div>
              <Label htmlFor="city">City</Label>
              <Input
                id="city"
                name="city"
                value={form.city ?? ''}
                onChange={(e) =>
                  setForm((f) => ({ ...f, city: e.target.value }))
                }
              />
            </div>
            <div>
              <Label htmlFor="country">Country</Label>
              <Input
                id="country"
                name="country"
                value={form.country ?? ''}
                onChange={(e) =>
                  setForm((f) => ({ ...f, country: e.target.value }))
                }
              />
            </div>
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <div>
              <Label htmlFor="salaryMin">Salary min</Label>
              <Input
                id="salaryMin"
                name="salaryMin"
                type="number"
                min={0}
                value={form.salaryMin ?? ''}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    salaryMin: e.target.value
                      ? Number(e.target.value)
                      : undefined,
                  }))
                }
              />
            </div>
            <div>
              <Label htmlFor="salaryMax">Salary max</Label>
              <Input
                id="salaryMax"
                name="salaryMax"
                type="number"
                min={0}
                value={form.salaryMax ?? ''}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    salaryMax: e.target.value
                      ? Number(e.target.value)
                      : undefined,
                  }))
                }
              />
            </div>
          </div>
          <div>
            <Label htmlFor="expiresAt">Expires at</Label>
            <Input
              id="expiresAt"
              name="expiresAt"
              type="datetime-local"
              required
              value={form.expiresAt ?? ''}
              onChange={(e) =>
                setForm((f) => ({ ...f, expiresAt: e.target.value }))
              }
            />
            <p className="mt-1 text-xs text-ink-muted">
              Required by current backend schema (nullable mismatch).
            </p>
          </div>
          <div>
            <Label htmlFor="requirements">Requirements</Label>
            <Textarea
              id="requirements"
              name="requirements"
              rows={3}
              value={form.requirements ?? ''}
              onChange={(e) =>
                setForm((f) => ({ ...f, requirements: e.target.value }))
              }
            />
          </div>
          <div>
            <Label htmlFor="benefits">Benefits</Label>
            <Textarea
              id="benefits"
              name="benefits"
              rows={3}
              value={form.benefits ?? ''}
              onChange={(e) =>
                setForm((f) => ({ ...f, benefits: e.target.value }))
              }
            />
          </div>
          <FieldError message={error} />
          <Button type="submit" disabled={save.isPending}>
            {save.isPending ? 'Saving…' : isEdit ? 'Save changes' : 'Post job'}
          </Button>
        </form>
      </Card>
    </div>
  )
}
