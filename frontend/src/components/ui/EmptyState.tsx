import { Button } from '@/components/ui/Button'

export function EmptyState({
  title,
  description,
  actionLabel,
  onAction,
}: {
  title: string
  description?: string
  actionLabel?: string
  onAction?: () => void
}) {
  return (
    <div className="rounded-[12px] border-[0.5px] border-border bg-surface px-6 py-12 text-center">
      <h3 className="font-serif text-xl text-ink">{title}</h3>
      {description ? (
        <p className="mx-auto mt-2 max-w-md text-sm text-ink-secondary">
          {description}
        </p>
      ) : null}
      {actionLabel && onAction ? (
        <Button className="mt-5" onClick={onAction}>
          {actionLabel}
        </Button>
      ) : null}
    </div>
  )
}
