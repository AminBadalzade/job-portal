import { useAuth } from '@/auth/AuthContext'
import { Avatar } from '@/components/ui/Avatar'
import { Button } from '@/components/ui/Button'
import { cn } from '@/lib/utils'
import { Menu, X } from 'lucide-react'
import { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'

type NavItem = { to: string; label: string; end?: boolean }

function seekerNav(): NavItem[] {
  return [
    { to: '/jobs', label: 'Jobs' },
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/applications', label: 'My applications' },
    { to: '/resumes', label: 'My resumes' },
    { to: '/profile', label: 'Profile' },
  ]
}

function employerNav(membership: string | null): NavItem[] {
  if (membership === 'OWNER') {
    return [
      { to: '/employer', label: 'Dashboard', end: true },
      { to: '/employer/company', label: 'My company' },
      { to: '/employer/jobs', label: 'My jobs' },
      { to: '/employer/join-requests', label: 'Join requests' },
      { to: '/profile', label: 'Profile' },
    ]
  }
  if (membership === 'STAFF') {
    return [
      { to: '/employer', label: 'Dashboard', end: true },
      { to: '/employer/jobs', label: 'My jobs' },
      { to: '/profile', label: 'Profile' },
    ]
  }
  return [
    { to: '/jobs', label: 'Jobs' },
    { to: '/employer', label: 'Dashboard', end: true },
    { to: '/employer/companies', label: 'Companies' },
    { to: '/employer/my-join-requests', label: 'My join requests' },
    { to: '/profile', label: 'Profile' },
  ]
}

function guestNav(): NavItem[] {
  return [
    { to: '/jobs', label: 'Jobs' },
    { to: '/login', label: 'Log in' },
    { to: '/register', label: 'Register' },
  ]
}

export function AppLayout() {
  const { user, membership, logout, loading } = useAuth()
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()

  const items: NavItem[] = !user
    ? guestNav()
    : user.role === 'JOB_SEEKER'
      ? seekerNav()
      : user.role === 'EMPLOYER'
        ? employerNav(membership)
        : guestNav()

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-page">
      <header className="border-b-[0.5px] border-border bg-page/95 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3 sm:px-6">
          <Link to={user ? (user.role === 'EMPLOYER' ? '/employer' : '/dashboard') : '/jobs'} className="font-serif text-2xl text-accent">
            Hearth
          </Link>

          <nav className="hidden items-center gap-1 md:flex">
            {items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  cn(
                    'rounded-[8px] px-3 py-2 text-sm text-ink-secondary hover:bg-avatar hover:text-ink',
                    isActive && 'bg-accent-soft text-accent-ink',
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
            {user ? (
              <div className="ml-2 flex items-center gap-2 border-l-[0.5px] border-border pl-3">
                <Avatar firstName={user.firstName} lastName={user.lastName} />
                <Button variant="ghost" size="sm" onClick={() => void handleLogout()}>
                  Log out
                </Button>
              </div>
            ) : null}
          </nav>

          <button
            type="button"
            className="rounded-[8px] p-2 text-ink md:hidden"
            onClick={() => setOpen((v) => !v)}
            aria-label="Toggle menu"
          >
            {open ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>

        {open ? (
          <nav className="border-t-[0.5px] border-border px-4 py-3 md:hidden">
            <div className="flex flex-col gap-1">
              {items.map((item) => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.end}
                  onClick={() => setOpen(false)}
                  className={({ isActive }) =>
                    cn(
                      'rounded-[8px] px-3 py-2.5 text-sm text-ink-secondary',
                      isActive && 'bg-accent-soft text-accent-ink',
                    )
                  }
                >
                  {item.label}
                </NavLink>
              ))}
              {user ? (
                <Button
                  variant="secondary"
                  className="mt-2"
                  onClick={() => {
                    setOpen(false)
                    void handleLogout()
                  }}
                >
                  Log out
                </Button>
              ) : null}
            </div>
          </nav>
        ) : null}
      </header>

      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-6">
        {loading ? (
          <div className="flex min-h-[40vh] items-center justify-center text-sm text-ink-muted">
            Loading…
          </div>
        ) : (
          <Outlet />
        )}
      </main>
    </div>
  )
}
