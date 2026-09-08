import { api } from '@/api/client'
import type {
  JobApplicationCompanyResponse,
  JobApplicationRequest,
  JobApplicationSeekerResponse,
} from '@/types'

export const applicationsApi = {
  apply(jobId: number, body: JobApplicationRequest) {
    return api
      .post<JobApplicationSeekerResponse>(
        `/api/jobs/${jobId}/applications`,
        body,
      )
      .then((r) => r.data)
  },
  mine() {
    return api
      .get<JobApplicationSeekerResponse[]>('/api/job-applications')
      .then((r) => r.data)
  },
  forJob(jobId: number) {
    // Use applications — NOT the dead /applicants stub
    return api
      .get<JobApplicationCompanyResponse[]>(`/api/jobs/${jobId}/applications`)
      .then((r) => r.data)
  },
  review(id: number) {
    return api
      .put<JobApplicationCompanyResponse>(`/api/job-applications/${id}/review`)
      .then((r) => r.data)
  },
  interview(id: number) {
    return api
      .put<JobApplicationCompanyResponse>(
        `/api/job-applications/${id}/interview`,
      )
      .then((r) => r.data)
  },
  accept(id: number) {
    return api
      .put<JobApplicationCompanyResponse>(`/api/job-applications/${id}/accept`)
      .then((r) => r.data)
  },
  reject(id: number) {
    return api
      .put<JobApplicationCompanyResponse>(`/api/job-applications/${id}/reject`)
      .then((r) => r.data)
  },
  withdraw(id: number) {
    return api.delete(`/api/job-applications/${id}`)
  },
}
