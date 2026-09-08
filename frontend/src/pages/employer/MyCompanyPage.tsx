import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { ConfirmDialog } from '@/components/ui/ConfirmDialog'
import { EmptyState } from '@/components/ui/EmptyState'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { PageHeader } from '@/components/ui/PageHeader'
import { Textarea } from '@/components/ui/Textarea'
import { FieldError } from '@/components/ui/PageHeader'
import { useMutation } from '@tanstack/react-query'
import { useEffect, useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

export function MyCompanyPage() {
  const { user, membership, refreshUser } = useAuth()
  const navigate = useNavigate()
  const [deleteOpen, setDeleteOpen] = useState(false)
  const [error, setError] = useState('')
  const [form, setForm] = useState({
    name: '',
    description: '',
    industry: '',
  })

  useEffect(() => {
    if (user?.company) {
      setForm({
        name: user.company.name,
        description: user.company.description,
        industry: user.company.industry,
      })
    }
  }, [user?.company])

  // companyId can be undefined here — guard the mutationFn itself,
  // since the hooks must still be declared unconditionally below
  const companyId = user?.company?.id

  const update = useMutation({
    mutationFn: () => {
      if (!companyId) throw new Error('No company selected')
      return companiesApi.update(companyId, {
        name: form.name,
        description: form.description,
        industry: form.industry,
      })
    },
    onSuccess: async () => {
      toast.success('Company updated')
      await refreshUser()
    },
    onError: (err) => setError(getApiError(err).message),
  })

  const remove = useMutation({
    mutationFn: () => {
      if (!companyId) throw new Error('No company selected')
      return companiesApi.remove(companyId)
    },
    onSuccess: async () => {
      toast.success('Company deleted')
      setDeleteOpen(false)
      await refreshUser()
      navigate('/employer')
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  // Guard AFTER all hooks are declared
  if (membership !== 'OWNER' || !user?.company) {
    return <Navigate to="/employer" replace />
  }

  return (
    <div className="mx-auto max-w-xl">
      <PageHeader
        title="My company"
        description="Owner settings for your company."
      />
      {!user.company ? (
        <EmptyState title="No company" />
      ) : (
        <Card className="p-5">
          <form
            className="space-y-4"
            onSubmit={(e) => {
              e.preventDefault()
              setError('')
              update.mutate()
            }}
          >
            <div>
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                name="name"
                maxLength={150}
                required
                value={form.name}
                onChange={(e) =>
                  setForm((f) => ({ ...f, name: e.target.value }))
                }
              />
            </div>
            <div>
              <Label htmlFor="industry">Industry</Label>
              <Input
                id="industry"
                name="industry"
                maxLength={100}
                required
                value={form.industry}
                onChange={(e) =>
                  setForm((f) => ({ ...f, industry: e.target.value }))
                }
              />
            </div>
            <div>
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                name="description"
                maxLength={3000}
                required
                rows={5}
                value={form.description}
                onChange={(e) =>
                  setForm((f) => ({ ...f, description: e.target.value }))
                }
              />
            </div>
            <FieldError message={error} />
            <div className="flex flex-wrap gap-2">
              <Button type="submit" disabled={update.isPending}>
                Save changes
              </Button>
              <Button
                type="button"
                variant="danger"
                onClick={() => setDeleteOpen(true)}
              >
                Delete company
              </Button>
            </div>
          </form>
        </Card>
      )}

      <ConfirmDialog
        open={deleteOpen}
        title="Delete company?"
        description="This deletes the company, its jobs, applications, and join requests."
        confirmLabel="Delete company"
        danger
        loading={remove.isPending}
        onCancel={() => setDeleteOpen(false)}
        onConfirm={() => remove.mutate()}
      />
    </div>
  )
}
