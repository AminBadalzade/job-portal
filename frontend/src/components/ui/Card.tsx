import { cn } from '@/lib/utils'
import type { HTMLAttributes } from 'react'

export function Card({
  className,
  ...props
}: HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        'rounded-[12px] border-[0.5px] border-border bg-surface',
        className,
      )}
      {...props}
    />
  )
}
