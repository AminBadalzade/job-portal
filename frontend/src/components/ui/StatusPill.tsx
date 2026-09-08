import { cn } from '@/lib/utils'
import type { ApplicationStatus, JoinRequestStatus } from '@/types'

const appStyles: Record<ApplicationStatus, string> = {
  PENDING: 'bg-status-pending-bg text-status-pending-fg',
  REVIEWED: 'bg-status-reviewed-bg text-status-reviewed-fg',
  INTERVIEW: 'bg-status-interview-bg text-status-interview-fg',
  ACCEPTED: 'bg-status-accepted-bg text-status-accepted-fg',
  REJECTED: 'bg-status-rejected-bg text-status-rejected-fg',
}

const joinStyles: Record<JoinRequestStatus, string> = {
  PENDING: 'bg-status-pending-bg text-status-pending-fg',
  APPROVED: 'bg-status-accepted-bg text-status-accepted-fg',
  REJECTED: 'bg-status-rejected-bg text-status-rejected-fg',
}

const appLabels: Record<ApplicationStatus, string> = {
  PENDING: 'Pending',
  REVIEWED: 'Reviewed',
  INTERVIEW: 'Interview',
  ACCEPTED: 'Accepted',
  REJECTED: 'Rejected',
}

const joinLabels: Record<JoinRequestStatus, string> = {
  PENDING: 'Pending',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
}

export function StatusPill({
  status,
  kind = 'application',
  className,
}: {
  status: ApplicationStatus | JoinRequestStatus
  kind?: 'application' | 'join'
  className?: string
}) {
  const styles =
    kind === 'join'
      ? joinStyles[status as JoinRequestStatus]
      : appStyles[status as ApplicationStatus]
  const label =
    kind === 'join'
      ? joinLabels[status as JoinRequestStatus]
      : appLabels[status as ApplicationStatus]

  return (
    <span
      className={cn(
        'inline-flex rounded-[20px] px-2.5 py-0.5 text-xs font-medium',
        styles,
        className,
      )}
    >
      {label}
    </span>
  )
}
