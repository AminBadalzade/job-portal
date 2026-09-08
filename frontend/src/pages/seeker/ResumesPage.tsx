import { getApiError } from '@/api/client'
import { resumesApi } from '@/api/resumes'
import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card'
import { ConfirmDialog } from '@/components/ui/ConfirmDialog'
import { EmptyState } from '@/components/ui/EmptyState'
import { PageHeader } from '@/components/ui/PageHeader'
import { PageLoader } from '@/components/ui/Spinner'
import { formatDate } from '@/lib/utils'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useCallback, useState } from 'react'
import { toast } from 'sonner'

export function ResumesPage() {
  const qc = useQueryClient()
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [dragOver, setDragOver] = useState(false)

  const query = useQuery({
    queryKey: ['resumes'],
    queryFn: () => resumesApi.list(),
  })

  const upload = useMutation({
    mutationFn: (file: File) => resumesApi.upload(file),
    onSuccess: () => {
      toast.success('Resume uploaded')
      void qc.invalidateQueries({ queryKey: ['resumes'] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  const remove = useMutation({
    mutationFn: (id: number) => resumesApi.remove(id),
    onSuccess: () => {
      toast.success('Resume deleted')
      setDeleteId(null)
      void qc.invalidateQueries({ queryKey: ['resumes'] })
    },
    onError: (err) => toast.error(getApiError(err).message),
  })

  const onFiles = useCallback(
    (files: FileList | null) => {
      const file = files?.[0]
      if (!file) return
      upload.mutate(file)
    },
    [upload],
  )

  return (
    <div>
      <PageHeader
        title="My resumes"
        description="Upload once, reuse when applying. PDF and Word files only."
      />

      <div
        className={`rounded-[12px] border border-dashed px-6 py-10 text-center ${
          dragOver ? 'border-accent bg-accent-soft' : 'border-border bg-surface'
        }`}
        onDragOver={(e) => {
          e.preventDefault()
          setDragOver(true)
        }}
        onDragLeave={() => setDragOver(false)}
        onDrop={(e) => {
          e.preventDefault()
          setDragOver(false)
          onFiles(e.dataTransfer.files)
        }}
      >
        <p className="font-serif text-xl text-ink">Drop a resume here</p>
        <p className="mt-1 text-sm text-ink-secondary">
          or choose a file from your device
        </p>
        <label className="mt-4 inline-flex cursor-pointer">
          <span className="rounded-[8px] bg-ink px-4 py-2.5 text-sm font-medium text-page">
            {upload.isPending ? 'Uploading…' : 'Upload resume'}
          </span>
          <input
            id="resume-file"
            name="resumeFile"
            type="file"
            className="hidden"
            accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            onChange={(e) => onFiles(e.target.files)}
          />
        </label>
      </div>

      <div className="mt-6">
        {query.isLoading ? <PageLoader /> : null}
        {query.isError ? (
          <EmptyState
            title="Could not load resumes"
            description={getApiError(query.error).message}
          />
        ) : null}
        {query.data?.length === 0 ? (
          <EmptyState
            title="No resumes yet"
            description="Upload a resume to start applying."
          />
        ) : null}
        {query.data && query.data.length > 0 ? (
          <Card className="overflow-hidden">
            {query.data.map((resume) => (
              <div
                key={resume.id}
                className="flex flex-col gap-3 border-b-[0.5px] border-border px-4 py-3.5 last:border-b-0 sm:flex-row sm:items-center sm:justify-between"
              >
                <div>
                  <p className="text-sm font-medium text-ink">
                    {resume.fileName}
                  </p>
                  <p className="text-xs text-ink-muted">
                    Uploaded {formatDate(resume.uploadedAt)}
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() =>
                      void resumesApi
                        .download(resume.id, resume.fileName)
                        .catch((err) => toast.error(getApiError(err).message))
                    }
                  >
                    Download
                  </Button>
                  <Button
                    variant="danger"
                    size="sm"
                    onClick={() => setDeleteId(resume.id)}
                  >
                    Delete
                  </Button>
                </div>
              </div>
            ))}
          </Card>
        ) : null}
      </div>

      <ConfirmDialog
        open={deleteId != null}
        title="Delete resume?"
        description="This permanently removes the file from your library."
        confirmLabel="Delete"
        danger
        loading={remove.isPending}
        onCancel={() => setDeleteId(null)}
        onConfirm={() => deleteId && remove.mutate(deleteId)}
      />
    </div>
  )
}
