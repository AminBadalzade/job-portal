import { getApiError } from '@/api/client'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { FieldError } from '@/components/ui/PageHeader'
import { cn } from '@/lib/utils'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

export function RegisterPage() {
  const { register, login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'JOB_SEEKER' as 'JOB_SEEKER' | 'EMPLOYER',
  })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (form.password.length < 8 || form.password.length > 50) {
      setError('Password must be 8–50 characters.')
      return
    }
    setLoading(true)
    try {
      await register(form)
      await login({ email: form.email, password: form.password })
      toast.success('Account created')
      navigate(form.role === 'EMPLOYER' ? '/employer' : '/dashboard', {
        replace: true,
      })
    } catch (err) {
      const apiErr = getApiError(err)
      setError(
        apiErr.errors?.length
          ? apiErr.errors.join(' · ')
          : apiErr.message,
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <h1 className="font-serif text-[32px] text-ink">Join Hearth</h1>
      <p className="mt-1 text-sm text-ink-secondary">
        Create an account as a job seeker or employer.
      </p>
      <Card className="mt-6 p-5">
        <form className="space-y-4" onSubmit={(e) => void onSubmit(e)}>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <Label htmlFor="firstName">First name</Label>
              <Input
                id="firstName"
                name="firstName"
                autoComplete="given-name"
                maxLength={40}
                required
                value={form.firstName}
                onChange={(e) =>
                  setForm((f) => ({ ...f, firstName: e.target.value }))
                }
              />
            </div>
            <div>
              <Label htmlFor="lastName">Last name</Label>
              <Input
                id="lastName"
                name="lastName"
                autoComplete="family-name"
                maxLength={40}
                required
                value={form.lastName}
                onChange={(e) =>
                  setForm((f) => ({ ...f, lastName: e.target.value }))
                }
              />
            </div>
          </div>
          <div>
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              name="email"
              type="email"
              autoComplete="email"
              maxLength={150}
              required
              value={form.email}
              onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
            />
          </div>
          <div>
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              name="password"
              type="password"
              autoComplete="new-password"
              minLength={8}
              maxLength={50}
              required
              value={form.password}
              onChange={(e) =>
                setForm((f) => ({ ...f, password: e.target.value }))
              }
            />
          </div>

          <fieldset>
            <legend className="mb-1.5 block text-sm text-ink-secondary">
              I am a
            </legend>
            <div className="mt-1 grid grid-cols-2 gap-2">
              {(
                [
                  { value: 'JOB_SEEKER', label: 'Job seeker' },
                  { value: 'EMPLOYER', label: 'Employer' },
                ] as const
              ).map((opt) => (
                <label
                  key={opt.value}
                  className={cn(
                    'cursor-pointer rounded-[8px] border-[0.5px] px-3 py-3 text-sm',
                    form.role === opt.value
                      ? 'border-accent bg-accent-soft text-accent-ink'
                      : 'border-border text-ink-secondary',
                  )}
                >
                  <input
                    type="radio"
                    id={`role-${opt.value}`}
                    name="role"
                    value={opt.value}
                    checked={form.role === opt.value}
                    onChange={() =>
                      setForm((f) => ({ ...f, role: opt.value }))
                    }
                    className="sr-only"
                  />
                  {opt.label}
                </label>
              ))}
            </div>
          </fieldset>

          <FieldError message={error} />
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Creating…' : 'Create account'}
          </Button>
        </form>
      </Card>
      <p className="mt-4 text-center text-sm text-ink-secondary">
        Already have an account?{' '}
        <Link to="/login" className="text-accent">
          Log in
        </Link>
      </p>
    </div>
  )
}
