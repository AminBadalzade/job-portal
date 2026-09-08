import { getApiError } from '@/api/client'
import { useAuth } from '@/auth/AuthContext'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { FieldError } from '@/components/ui/PageHeader'
import { Input } from '@/components/ui/Input'
import { Label } from '@/components/ui/Label'
import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError('')
    if (!email.trim() || password.length < 8) {
      setError('Enter a valid email and password (min 8 characters).')
      return
    }
    setLoading(true)
    try {
      const user = await login({ email, password })
      toast.success('Welcome back')
      if (from) navigate(from, { replace: true })
      else navigate(user.role === 'EMPLOYER' ? '/employer' : '/dashboard', { replace: true })
    } catch (err) {
      setError(getApiError(err).message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <h1 className="font-serif text-[32px] text-ink">Welcome back</h1>
      <p className="mt-1 text-sm text-ink-secondary">
        Log in to continue on Hearth.
      </p>
      <Card className="mt-6 p-5">
        <form className="space-y-4" onSubmit={(e) => void onSubmit(e)}>
          <div>
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              name="email"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              maxLength={150}
              required
            />
          </div>
          <div>
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              name="password"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              minLength={8}
              required
            />
          </div>
          <FieldError message={error} />
          <Button type="submit" className="w-full" disabled={loading}>
            {loading ? 'Signing in…' : 'Log in'}
          </Button>
        </form>
      </Card>
      <p className="mt-4 text-center text-sm text-ink-secondary">
        New here?{' '}
        <Link to="/register" className="text-accent">
          Create an account
        </Link>
      </p>
    </div>
  )
}
