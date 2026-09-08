import { AppLayout } from '@/components/layout/AppLayout'
import { GuestOnlyRoute, ProtectedRoute } from '@/routes/ProtectedRoute'
import { CompanyPage } from '@/pages/CompanyPage'
import { JobDetailPage } from '@/pages/JobDetailPage'
import { JobsPage } from '@/pages/JobsPage'
import { LoginPage } from '@/pages/LoginPage'
import { ProfilePage } from '@/pages/ProfilePage'
import { RegisterPage } from '@/pages/RegisterPage'
import { ApplicationsPage } from '@/pages/seeker/ApplicationsPage'
import { ResumesPage } from '@/pages/seeker/ResumesPage'
import { SeekerDashboardPage } from '@/pages/seeker/SeekerDashboardPage'
import { CompaniesSearchPage } from '@/pages/employer/CompaniesSearchPage'
import { CreateCompanyPage } from '@/pages/employer/CreateCompanyPage'
import { EmployerDashboardPage } from '@/pages/employer/EmployerDashboardPage'
import { JobApplicantsPage } from '@/pages/employer/JobApplicantsPage'
import { JobFormPage } from '@/pages/employer/JobFormPage'
import { JoinRequestsInboxPage } from '@/pages/employer/JoinRequestsInboxPage'
import { MyCompanyPage } from '@/pages/employer/MyCompanyPage'
import { MyJobsPage } from '@/pages/employer/MyJobsPage'
import { MyJoinRequestsPage } from '@/pages/employer/MyJoinRequestsPage'
import { Navigate, Route, Routes } from 'react-router-dom'

export function AppRouter() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<Navigate to="/jobs" replace />} />
        <Route path="jobs" element={<JobsPage />} />
        <Route path="jobs/:id" element={<JobDetailPage />} />
        <Route path="companies/:id" element={<CompanyPage />} />

        <Route element={<GuestOnlyRoute />}>
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />
        </Route>

        <Route element={<ProtectedRoute />}>
          <Route path="profile" element={<ProfilePage />} />
        </Route>

        <Route element={<ProtectedRoute roles={['JOB_SEEKER']} />}>
          <Route path="dashboard" element={<SeekerDashboardPage />} />
          <Route path="applications" element={<ApplicationsPage />} />
          <Route path="resumes" element={<ResumesPage />} />
        </Route>

        <Route element={<ProtectedRoute roles={['EMPLOYER']} />}>
          <Route path="employer" element={<EmployerDashboardPage />} />
          <Route
            path="employer/create-company"
            element={<CreateCompanyPage />}
          />
          <Route path="employer/companies" element={<CompaniesSearchPage />} />
          <Route
            path="employer/my-join-requests"
            element={<MyJoinRequestsPage />}
          />
        </Route>

        <Route
          element={
            <ProtectedRoute
              roles={['EMPLOYER']}
              memberships={['OWNER']}
            />
          }
        >
          <Route path="employer/company" element={<MyCompanyPage />} />
          <Route
            path="employer/join-requests"
            element={<JoinRequestsInboxPage />}
          />
        </Route>

        <Route
          element={
            <ProtectedRoute
              roles={['EMPLOYER']}
              memberships={['OWNER', 'STAFF']}
            />
          }
        >
          <Route path="employer/jobs" element={<MyJobsPage />} />
          <Route path="employer/jobs/new" element={<JobFormPage />} />
          <Route path="employer/jobs/:id/edit" element={<JobFormPage />} />
          <Route
            path="employer/jobs/:id/applicants"
            element={<JobApplicantsPage />}
          />
        </Route>

        <Route path="*" element={<Navigate to="/jobs" replace />} />
      </Route>
    </Routes>
  )
}
