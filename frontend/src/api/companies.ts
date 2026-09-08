import { api } from '@/api/client'
import type {
  CompanyJoinRequestResponse,
  CompanyRequest,
  CompanyResponse,
  CompanySummaryResponse,
  JobSummaryResponse,
  UpdateCompanyRequest,
} from '@/types'

export const companiesApi = {
  list(name?: string) {
    return api
      .get<CompanySummaryResponse[]>('/api/companies', {
        params: name ? { name } : undefined,
      })
      .then((r) => r.data)
  },
  getById(id: number) {
    return api.get<CompanyResponse>(`/api/companies/${id}`).then((r) => r.data)
  },
  getJobs(id: number) {
    return api
      .get<JobSummaryResponse[]>(`/api/companies/${id}/jobs`)
      .then((r) => r.data)
  },
  create(body: CompanyRequest) {
    return api.post<CompanyResponse>('/api/companies', body).then((r) => r.data)
  },
  update(id: number, body: UpdateCompanyRequest) {
    return api
      .put<CompanyResponse>(`/api/companies/${id}`, body)
      .then((r) => r.data)
  },
  remove(id: number) {
    return api.delete(`/api/companies/${id}`)
  },
  join(companyId: number) {
    return api
      .post<CompanyJoinRequestResponse>(`/api/companies/${companyId}/join`)
      .then((r) => r.data)
  },
  listJoinRequests(companyId: number) {
    return api
      .get<CompanyJoinRequestResponse[]>(
        `/api/companies/${companyId}/join-requests`,
      )
      .then((r) => r.data)
  },
  approveJoinRequest(companyId: number, requestId: number) {
    return api
      .put<CompanyJoinRequestResponse>(
        `/api/companies/${companyId}/join-requests/${requestId}/approve`,
      )
      .then((r) => r.data)
  },
  rejectJoinRequest(companyId: number, requestId: number) {
    return api
      .put<CompanyJoinRequestResponse>(
        `/api/companies/${companyId}/join-requests/${requestId}/reject`,
      )
      .then((r) => r.data)
  },
}
