import { api } from '@/api/client'
import type {
  CreateJobRequest,
  JobResponse,
  JobSearchRequest,
  JobSummaryResponse,
  PageResponse,
  UpdateJobRequest,
} from '@/types'

export const jobsApi = {
  getById(id: number) {
    return api.get<JobResponse>(`/api/jobs/${id}`).then((r) => r.data)
  },
  search(
    body: JobSearchRequest,
    params: {
      pageNo?: number
      pageSize?: number
      sortBy?: string
      sortDirection?: 'ASC' | 'DESC'
    } = {},
  ) {
    // TEMP: verify filter payload — remove after confirming in DevTools
    console.log('[jobsApi.search] payload', body)
    return api
      .post<PageResponse<JobSummaryResponse>>('/api/jobs/search', body, {
        params: {
          pageNo: params.pageNo ?? 0,
          pageSize: params.pageSize ?? 10,
          sortBy: params.sortBy ?? 'createdAt',
          sortDirection: params.sortDirection ?? 'DESC',
        },
      })
      .then((r) => r.data)
  },
  create(body: CreateJobRequest) {
    return api.post<JobResponse>('/api/jobs', body).then((r) => r.data)
  },
  update(id: number, body: UpdateJobRequest) {
    return api.put<JobResponse>(`/api/jobs/${id}`, body).then((r) => r.data)
  },
  remove(id: number) {
    return api.delete(`/api/jobs/${id}`)
  },
}
