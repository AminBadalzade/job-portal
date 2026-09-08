import { api, tokenStorage } from '@/api/client'
import type { ResumeResponse } from '@/types'

export const resumesApi = {
  list() {
    return api.get<ResumeResponse[]>('/api/resumes').then((r) => r.data)
  },
  upload(file: File) {
    const form = new FormData()
    form.append('file', file)
    return api
      .post<ResumeResponse>('/api/resumes', form, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((r) => r.data)
  },
  remove(id: number) {
    return api.delete(`/api/resumes/${id}`)
  },
  /** Download via authorized endpoint — ignore storage-key downloadUrl field */
  async download(id: number, fileName: string) {
    const response = await api.get(`/api/resumes/${id}/download`, {
      responseType: 'blob',
    })
    const url = URL.createObjectURL(response.data)
    const a = document.createElement('a')
    a.href = url
    a.download = fileName
    a.click()
    URL.revokeObjectURL(url)
  },
  downloadUrl(id: number) {
    const base = import.meta.env.VITE_API_BASE_URL || ''
    return `${base}/api/resumes/${id}/download`
  },
  authHeader() {
    const token = tokenStorage.getAccess()
    return token ? { Authorization: `Bearer ${token}` } : {}
  },
}
