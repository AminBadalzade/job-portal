import { Button } from '@/components/ui/Button'
import { Modal } from '@/components/ui/Modal'
import { formatDate } from '@/lib/utils'
import type { ResumeResponse } from '@/types'
import { Link } from 'react-router-dom'
import { useState } from 'react'

export function ApplyModal({
  open,
  resumes,
  loading,
  onClose,
  onApply,
}: {
  open: boolean
  resumes: ResumeResponse[]
  loading?: boolean
  onClose: () => void
  onApply: (resumeId: number) => void
}) {
  const [selected, setSelected] = useState<number | null>(null)

  return (
    <Modal open={open} title="Apply with a resume" onClose={onClose}>
      {resumes.length === 0 ? (
        <div className="space-y-3 text-sm text-ink-secondary">
          <p>You need a resume in your library before applying.</p>
          <Link to="/resumes" className="text-accent" onClick={onClose}>
            Go to my resumes
          </Link>
        </div>
      ) : (
        <div className="space-y-2">
          <p className="text-sm text-ink-secondary">
            Select an existing resume from your library.
          </p>
          {resumes.map((r) => (
            <label
              key={r.id}
              className={`flex cursor-pointer items-center gap-3 rounded-[8px] border-[0.5px] px-3 py-3 ${
                selected === r.id
                  ? 'border-accent bg-accent-soft'
                  : 'border-border'
              }`}
            >
              <input
                type="radio"
                id={`resume-${r.id}`}
                name="resume"
                checked={selected === r.id}
                onChange={() => setSelected(r.id)}
              />
              <div>
                <p className="text-sm font-medium text-ink">{r.fileName}</p>
                <p className="text-xs text-ink-muted">
                  Uploaded {formatDate(r.uploadedAt)}
                </p>
              </div>
            </label>
          ))}
          <Button
            className="mt-4 w-full"
            disabled={!selected || loading}
            onClick={() => selected && onApply(selected)}
          >
            Submit application
          </Button>
        </div>
      )}
    </Modal>
  )
}
