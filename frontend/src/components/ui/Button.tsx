import { cn } from '@/lib/utils'
import type { ButtonHTMLAttributes } from 'react'

type Variant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'accent'

const variants: Record<Variant, string> = {
  primary:
    'bg-ink text-page hover:bg-ink/90 disabled:bg-ink-muted',
  secondary:
    'bg-transparent text-ink border border-border hover:bg-avatar',
  ghost: 'bg-transparent text-ink-secondary hover:bg-avatar',
  danger: 'bg-status-rejected-bg text-status-rejected-fg hover:opacity-90',
  accent: 'bg-accent text-page hover:bg-accent/90',
}

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  size?: 'sm' | 'md'
}

export function Button({
  className,
  variant = 'primary',
  size = 'md',
  type = 'button',
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded-[8px] font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-60',
        size === 'sm' ? 'px-3 py-1.5 text-sm' : 'px-4 py-2.5 text-sm',
        variants[variant],
        className,
      )}
      {...props}
    />
  )
}
