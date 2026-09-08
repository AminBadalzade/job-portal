import { getApiError } from '@/api/client'
import { companiesApi } from '@/api/companies'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { PageHeader } from '@/components/ui/PageHeader'
import { Textarea } from '@/components/ui/Textarea'
import { FieldError } from '@/components/ui/PageHeader'
import { useMutation } from '@tanstack/react-query'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

export function CreateCompanyPage() {
  const { refreshUser } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    name: '',
    description: '',
    industry: '',
    logoUrl: '',
  })
  const [error, setError] = useState('')

  const mutation = useMutation({
    mutationFn: () =>
      companiesApi.create({
        name: form.name,
        description: form.description,
        industry: form.industry,
        logoUrl: form.logoUrl || undefined,
      }),
    onSuccess: async () => {
      toast.success('Company created')
      await refreshUser()
      navigate('/employer')
    },
    onError: (err) => setError(getApiError(err).message),
  })

  return (
    <div className="mx-auto max-w-xl">
      <PageHeader
        title="Create a company"
        description="You will become the owner of this company."
      />
      <Card className="p-5">
        <form
          className="space-y-4"
          onSubmit={(e) => {
            e.preventDefault()
            setError('')
            mutation.mutate()
          }}
        >
          <div>
            <Label htmlFor="name">Company name</Label>
            <Input
              id="name"
              name="name"
              maxLength={150}
              required
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
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
          <div>
            <Label htmlFor="logoUrl">Logo URL (optional)</Label>
            <Input
              id="logoUrl"
              name="logoUrl"
              value={form.logoUrl}
              onChange={(e) =>
                setForm((f) => ({ ...f, logoUrl: e.target.value }))
              }
            />
          </div>
          <FieldError message={error} />
          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? 'Creating…' : 'Create company'}
          </Button>
        </form>
      </Card>
    </div>
  )
}
