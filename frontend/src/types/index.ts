export type Role = 'ADMIN' | 'EMPLOYER' | 'JOB_SEEKER'

export type ApplicationStatus =
  | 'PENDING'
  | 'REVIEWED'
  | 'INTERVIEW'
  | 'ACCEPTED'
  | 'REJECTED'

export type JoinRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED'

export type JobStatus = 'DRAFT' | 'OPEN' | 'CLOSED' | 'EXPIRED'

export type ExperienceLevel = 'JUNIOR' | 'MID' | 'SENIOR' | 'LEAD'

export type WorkType = 'REMOTE' | 'HYBRID' | 'ON_SITE'

export type EmploymentType =
  | 'FULL_TIME'
  | 'PART_TIME'
  | 'CONTRACT'
  | 'INTERNSHIP'

export type EmployerMembership = 'NONE' | 'OWNER' | 'STAFF'

export interface ErrorResponse {
  message: string
  status: number
  timestamp: string
  path: string
  errors?: string[] | null
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
}

export interface RegisterRequest {
  firstName: string
  lastName: string
  email: string
  password: string
  role: 'JOB_SEEKER' | 'EMPLOYER'
}

export interface LoginRequest {
  email: string
  password: string
}

export interface CompanySummaryResponse {
  id: number
  name: string
  logoUrl?: string | null
  industry?: string | null
}

/** Public company DTO — no ownerId by design */
export interface CompanyResponse {
  id: number
  name: string
  description: string
  industry: string
  logoUrl?: string | null
}

/** Nested on /api/me only — includes ownerId */
export interface MyCompanyResponse {
  id: number
  name: string
  description: string
  industry: string
  logoUrl?: string | null
  ownerId: number
}

export interface UserResponse {
  id: number
  firstName: string
  lastName: string
  email: string
  role: Role
  company?: MyCompanyResponse
}

export interface UserSummaryResponse {
  id: number
  firstName: string
  lastName: string
  email: string
}

export interface CompanyRequest {
  name: string
  description: string
  industry: string
  logoUrl?: string
}

export interface UpdateCompanyRequest {
  name?: string
  description?: string
  industry?: string
}

export interface JobSummaryResponse {
  id: number
  title: string
  companyName: string
  city?: string | null
  country?: string | null
  salaryMin?: number | null
  salaryMax?: number | null
  experienceLevel: ExperienceLevel
  workType: WorkType
  employmentType: EmploymentType
  createdAt: string
}

export interface JobResponse {
  id: number
  title: string
  description: string
  salaryMin?: number | null
  salaryMax?: number | null
  experienceLevel: ExperienceLevel
  workType: WorkType
  employmentType: EmploymentType
  city?: string | null
  country?: string | null
  requirements?: string | null
  benefits?: string | null
  createdAt: string
  updatedAt: string
  expiresAt?: string | null
  status: JobStatus
  company: CompanySummaryResponse
}

export interface JobSearchRequest {
  title?: string
  city?: string
  country?: string
  experienceLevel?: ExperienceLevel
  workType?: WorkType
  employmentType?: EmploymentType
  salaryMin?: number
  salaryMax?: number
}

export interface CreateJobRequest {
  title: string
  description: string
  salaryMin?: number | null
  salaryMax?: number | null
  experienceLevel: ExperienceLevel
  workType: WorkType
  employmentType: EmploymentType
  city?: string
  country?: string
  requirements?: string
  benefits?: string
  expiresAt?: string
}

export type UpdateJobRequest = Partial<CreateJobRequest>

export interface ResumeResponse {
  id: number
  fileName: string
  /** Backend currently maps storage key here — use download endpoint instead */
  downloadUrl: string
  uploadedAt: string
}

export interface JobApplicationSeekerResponse {
  id: number
  status: ApplicationStatus
  appliedAt: string
  job: JobSummaryResponse
  resume: ResumeResponse
}

export interface JobApplicationCompanyResponse {
  id: number
  status: ApplicationStatus
  appliedAt: string
  applicant: UserSummaryResponse
  resume: ResumeResponse
}

export interface JobApplicationRequest {
  resumeId: number
}

export interface CompanyJoinRequestResponse {
  id: number
  userId: number
  firstName: string
  lastName: string
  status: JoinRequestStatus
  createdAt: string
}

export interface EmployerJoinRequestResponse {
  id: number
  companyId: number
  companyName: string
  status: JoinRequestStatus
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}
