import { api } from '@/api/client'
import type { EmployerJoinRequestResponse } from '@/types'

export const joinRequestsApi = {
  mine() {
    return api
      .get<EmployerJoinRequestResponse[]>('/api/join-requests/me')
      .then((r) => r.data)
  },
}
