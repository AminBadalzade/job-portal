import { cn, initials } from '@/lib/utils'

export function Avatar({
  firstName,
  lastName,
  name,
  className,
}: {
  firstName?: string
  lastName?: string
  name?: string
  className?: string
}) {
  const text = name
    ? name
        .split(' ')
        .slice(0, 2)
        .map((p) => p[0])
        .join('')
        .toUpperCase()
    : initials(firstName, lastName)

  return (
    <div
      className={cn(
        'flex h-9 w-9 shrink-0 items-center justify-center rounded-[9px] bg-avatar text-xs font-medium text-ink-secondary',
        className,
      )}
    >
      {text}
    </div>
  )
}
