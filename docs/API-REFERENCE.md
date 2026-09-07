# Job Portal API Reference (for Frontend Generation)

This document describes the current backend API contract and the required frontend behavior,
verified directly against source code (controllers, DTOs, exception handler).

**Sections marked ⚠️ identify confirmed backend issues or inconsistencies that should be
fixed before production.** The frontend must not implement workarounds for issues explicitly
marked as requiring a backend fix — do not build around a known gap; flag it and wait for the
backend to be corrected instead. Do not infer behavior beyond what's stated here.

---

## 1. Registration & Role Restriction

Public registration supports only `JOB_SEEKER` and `EMPLOYER`. `ADMIN` cannot be created
through the public registration endpoint.

- The frontend must not display `ADMIN` as a registration option.
- **The backend now also rejects it** — `UserServiceImpl.register()` explicitly checks
  `request.getRole() == Role.ADMIN` and throws `ForbiddenException` before any user is
  created. This closes the gap flagged in the previous version of this document.
- Frontend restrictions are not a security boundary on their own — the backend check above
  is what actually matters; the frontend hiding the option is just good UX.
- ADMIN accounts must be provisioned through a separate, trusted mechanism outside this
  registration flow (e.g. direct DB seeding, a separate internal endpoint, etc. — not
  covered by this document).

**Expected response when attempted anyway:** `POST /api/register` with `role: "ADMIN"` →
`403 Forbidden` → `ErrorResponse` with message `"ADMIN registration is not allowed"`.

**⚠️ One thing to verify:** the duplicate-email check in the same method throws
`IllegalArgumentException`, not one of this project's custom exceptions:
```java
if (userRepository.existsUserByEmail(request.getEmail())) {
    throw new IllegalArgumentException("Such email registered before");
}
```
`GlobalExceptionHandler` has no handler for `IllegalArgumentException` specifically — it will
fall through to the generic `Exception` catch-all, returning a **generic 500** with the
message "Something went wrong" rather than a proper `409 Conflict` naming the real issue.
Recommended fix — either add a dedicated handler:
```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request){
    ErrorResponse response = new ErrorResponse(
            ex.getMessage(), HttpStatus.CONFLICT.value(), LocalDateTime.now(), request.getRequestURI());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
}
```
or, more consistent with the rest of the codebase, swap this one line to throw
`ConflictException` instead, matching the pattern used everywhere else in this project
(join requests, company creation, application duplicates all use `ConflictException` for
this exact kind of "this already exists" case).

---

## 2. Entities & Business Logic Summary

*(unchanged from prior version — see sections 3–5 of the previous reference for the full
state machine, ownership/staff model, and resume-library behavior. Summarized here for context.)*

- **User** — `Role` (`ADMIN` / `EMPLOYER` / `JOB_SEEKER`), optional `Company` affiliation.
- **Company** — one `owner`, any number of staff.
- **CompanyJoinRequest** — `User` + `Company` + `JoinRequestStatus` (`PENDING`/`APPROVED`/`REJECTED`).
- **Job** — belongs to a `Company` (derived server-side from the authenticated user, **not**
  supplied by the client in the request body).
- **Resume** — belongs to a `User`; reusable across applications.
- **JobApplication** — `User` + `Job` + `Resume` + `ApplicationStatus`. Unique on (`user_id`, `job_id`).

**Status state machine (order-enforced, `409 Conflict` on invalid transition):**
```
PENDING → REVIEWED → INTERVIEW → ACCEPTED
                              → REJECTED (from most non-terminal states)
```

---

## 3. Authentication Behavior

*(See prior version for the full refresh-token flow and route-protection rules — unchanged.)*

**Confirmed against `AuthController`:**
- `POST /api/login` → `200 OK` → `AuthResponse` (`accessToken` + `refreshToken`)
- `POST /api/refresh` → `200 OK` → `AuthResponse` (issues a new `accessToken`, **reuses the
  same `refreshToken`** — it does not rotate the refresh token on each use)
- `POST /api/logout` → `204 No Content`
- **Neither `/api/refresh` nor `/api/logout` require an `Authorization: Bearer` header** —
  both authenticate purely via the `refreshToken` in the request body. Don't attach an
  (possibly expired) access token to these two calls; it's not checked.
- `TokenResponse` is unused dead code — `AuthResponse` is the real response DTO for both
  login and refresh. Safe to delete `TokenResponse` from the codebase.

---

## 4. `GET /api/me` — Source of Truth for Current User

Returns `UserResponse`. `company` is **omitted entirely from the JSON** (not `null`) when the
user has no company, due to `@JsonInclude(NON_NULL)` — check for the key's absence, not
strictly `=== null`, depending on your HTTP client's JSON parsing behavior.

```json
{
  "id": 10,
  "firstName": "Amin",
  "lastName": "Karimov",
  "email": "amin@example.com",
  "role": "EMPLOYER",
  "company": {
    "id": 5,
    "name": "Example",
    "description": "...",
    "industry": "Technology",
    "logoUrl": "https://..."
  }
}
```

**⚠️ Confirmed gap — needs a backend fix before the ownership logic below can work:**
`CompanyResponse` currently has **no `ownerId` field** (`id`, `name`, `description`, `industry`,
`logoUrl` only). The owner/staff derivation this document specifies requires it. Add it via
your `CompanyMapper`:

```java
@Mapping(source = "owner.id", target = "ownerId")
CompanyResponse toResponse(Company entity);
```

and add the matching field to the DTO:
```java
private Long ownerId;
```

This is a small, self-contained fix — once in, `/api/me`'s nested `company` object will
include `ownerId`, and the derivation logic below works exactly as specified.

---

## 5. Employer Company Membership Logic

*(Logic unchanged from prior version — reproduced here for completeness. **Blocked on the
`ownerId` field being added to `CompanyResponse`, per the fix in section 4.**)*

```
user.company is absent/null
    → NO COMPANY

user.company.ownerId == user.id
    → OWNER

otherwise
    → STAFF
```

Derive this once from `/api/me`, store in global auth state, reuse across the app. Do not
call a separate endpoint or interpret a `403` to determine ownership.

---

## 6. DTO Reference

### Request DTOs

| DTO | Field | Type | Constraints |
|---|---|---|---|
| `RegisterRequest` | firstName | String | `@NotBlank`, max 40 |
| | lastName | String | `@NotBlank`, max 40 |
| | email | String | `@NotBlank`, `@Email`, max 150 |
| | password | String | `@NotBlank`, 8–50 chars |
| | role | `Role` | `@NotNull` — **frontend must restrict to `JOB_SEEKER`/`EMPLOYER`** |
| `LoginRequest` | email | String | `@NotBlank`, `@Email`, max 150 |
| | password | String | `@NotBlank`, min 8 |
| `RefreshTokenRequest` | refreshToken | String | — |
| `CompanyRequest` | name | String | `@NotBlank`, max 150 |
| | description | String | `@NotBlank`, max 3000 |
| | industry | String | `@NotBlank`, max 100 |
| | logoUrl | String | optional |
| `UpdateCompanyRequest` | name | String | max 150, optional (partial update) |
| | description | String | max 3000, optional |
| | industry | String | max 100, optional |
| `CreateJobRequest` | title | String | `@NotBlank` |
| | description | String | `@NotBlank` |
| | salaryMin / salaryMax | BigDecimal | optional |
| | experienceLevel | `ExperienceLevel` | `@NotNull` |
| | workType | `WorkType` | `@NotNull` |
| | employmentType | `EmploymentType` | `@NotNull` |
| | city / country | String | optional |
| | requirements / benefits | String | optional |
| | expiresAt | LocalDateTime | optional |
| | *(no `companyId` — derived from the authenticated user's company server-side)* | | |
| `UpdateJobRequest` | *(all fields above, all optional — partial update pattern)* | | `expiresAt` has `@FutureOrPresent` |
| `JobSearchRequest` | title / city / country | String | max 100 each, optional |
| | experienceLevel / workType / employmentType | enums | optional |
| | salaryMin / salaryMax | BigDecimal | `@PositiveOrZero`, optional |
| `JobApplicationRequest` | resumeId | Long | `@NotNull` |
| `UpdateApplicationStatusRequest` | status | `ApplicationStatus` | `@NotNull` — **⚠️ not currently wired to any controller endpoint; the actual status-transition endpoints (`/review`, `/interview`, `/accept`, `/reject`) take no body at all.** Likely unused/legacy — confirm before referencing it. |
| `UpdateUserRequest` | firstName / lastName | String | max 40, optional — **⚠️ not currently wired to any controller endpoint found in this reference.** |

### Response DTOs

| DTO | Fields |
|---|---|
| `UserResponse` | id, firstName, lastName, email, role, company (`CompanyResponse`, omitted if null) |
| `UserSummaryResponse` | id, firstName, lastName, email |
| `AuthResponse` | accessToken, refreshToken |
| `TokenResponse` | accessToken *(⚠️ see note in section 3 — may be superseded by `AuthResponse`)* |
| `CompanyResponse` | id, name, description, industry, logoUrl — **⚠️ missing `ownerId`, add per section 4's fix** |
| `CompanySummaryResponse` | id, name, logoUrl, industry |
| `CompanyJoinRequestResponse` | id, userId, firstName, lastName, status, createdAt |
| `EmployerJoinRequestResponse` | id, companyId, companyName, status, createdAt |
| `JobResponse` | id, title, description, salaryMin, salaryMax, experienceLevel, workType, employmentType, city, country, requirements, benefits, createdAt, updatedAt, expiresAt, status, company (`CompanySummaryResponse`) |
| `JobSummaryResponse` | id, title, companyName, city, country, salaryMin, salaryMax, experienceLevel, workType, employmentType, createdAt |
| `JobApplicationSeekerResponse` | id, status, appliedAt, job (`JobSummaryResponse`), resume (`ResumeResponse`) |
| `JobApplicationCompanyResponse` | id, status, appliedAt, applicant (`UserSummaryResponse`), resume (`ResumeResponse`) |
| `ResumeResponse` | id, fileName, downloadUrl, uploadedAt |

---

## 7. Endpoint Reference — Request, Response, and Status Codes

### Authentication

| Endpoint | Auth | Request Body | Success | Errors |
|---|---|---|---|---|
| `POST /api/register` | none | `RegisterRequest` | `200 OK` → `UserResponse` *(note: not `201 Created`, despite creating a resource — inconsistent with REST convention, worth fixing)* | `400` validation failure · `403` `role: ADMIN` attempted · `500` duplicate email *(⚠️ should be `409` — see section 1, `IllegalArgumentException` isn't handled as a proper conflict yet)* |
| `POST /api/login` | none | `LoginRequest` | `200 OK` → `AuthResponse` | `401` bad credentials |
| `POST /api/refresh` | **none** (no bearer header — auth is via body) | `RefreshTokenRequest` | `200 OK` → `AuthResponse` (new `accessToken`, same `refreshToken`) | `401` invalid/expired/revoked refresh token |
| `POST /api/logout` | **none** (no bearer header — auth is via body) | `RefreshTokenRequest` | `204 No Content` | — |
| `GET /api/me` | bearer | — | `200 OK` → `UserResponse` | `401` unauthenticated |

### Companies

| Endpoint | Auth | Request Body | Success | Errors |
|---|---|---|---|---|
| `GET /api/companies?name=` | none | — | `200 OK` → `List<CompanySummaryResponse>` | — |
| `GET /api/companies/{id}` | none | — | `200 OK` → `CompanyResponse` | `404` not found |
| `GET /api/companies/{id}/jobs` | none | — | `200 OK` → `List<JobSummaryResponse>` | `404` not found |
| `POST /api/companies` | bearer, `EMPLOYER` | `CompanyRequest` | `201 Created` → `CompanyResponse` | `400` validation · `403` role · `409` already has a company / name taken |
| `PUT /api/companies/{id}` | bearer, `EMPLOYER` | `UpdateCompanyRequest` | `200 OK` → `CompanyResponse` | `400` · `403` not owner / role · `404` not found |
| `DELETE /api/companies/{id}` | bearer, `EMPLOYER` | — | `204 No Content` | `403` not owner / role · `404` not found |

### Join Requests

| Endpoint | Auth | Request Body | Success | Errors |
|---|---|---|---|---|
| `POST /api/companies/{companyId}/join` | bearer, `EMPLOYER` | — | `201 Created` → `CompanyJoinRequestResponse` | `403` role · `404` company not found · `409` already affiliated / duplicate pending request |
| `GET /api/companies/{companyId}/join-requests` | bearer, `EMPLOYER` (owner) | — | `200 OK` → `List<CompanyJoinRequestResponse>` | `403` not owner · `404` not found |
| `PUT /api/companies/{companyId}/join-requests/{requestId}/approve` | bearer, `EMPLOYER` (owner) | — | `200 OK` → `CompanyJoinRequestResponse` | `403` not owner · `404` · `409` already processed |
| `PUT /api/companies/{companyId}/join-requests/{requestId}/reject` | bearer, `EMPLOYER` (owner) | — | `200 OK` → `CompanyJoinRequestResponse` | `403` not owner · `404` · `409` already processed |
| `GET /api/join-requests/me` | bearer, `EMPLOYER` | — | `200 OK` → `List<EmployerJoinRequestResponse>` | `403` role |

### Jobs

| Endpoint | Auth | Request Body | Success | Errors |
|---|---|---|---|---|
| `GET /api/jobs/{id}` | none | — | `200 OK` → `JobResponse` | `404` not found |
| `POST /api/jobs/search?pageNo=&pageSize=&sortBy=&sortDirection=` | none | `JobSearchRequest` | `200 OK` → `Page<JobSummaryResponse>` | `400` validation |
| `POST /api/jobs` | bearer, `EMPLOYER` | `CreateJobRequest` | `201 Created` → `JobResponse` | `400` · `403` role / no company |
| `PUT /api/jobs/{id}` | bearer, `EMPLOYER` | `UpdateJobRequest` | `200 OK` → `JobResponse` | `400` · `403` not staff of the job's company · `404` |
| `DELETE /api/jobs/{jobId}` | bearer, `EMPLOYER` or `ADMIN` | — | `204 No Content` | `403` · `404` |
| `GET /api/jobs/{jobId}/applicants` | bearer, `EMPLOYER` | — | **⚠️ Currently returns `null` — dead/broken stub. Use `GET /api/jobs/{jobId}/applications` instead (see below). This endpoint should be removed.** | |

### Applications

| Endpoint | Auth | Request Body | Success | Errors |
|---|---|---|---|---|
| `POST /api/jobs/{jobId}/applications` | bearer, `JOB_SEEKER` | `JobApplicationRequest` | `201 Created` → `JobApplicationSeekerResponse` | `400` · `403` resume not owned by caller · `404` job/resume not found · `409` job closed/expired, duplicate application |
| `GET /api/job-applications` | bearer, `JOB_SEEKER` | — | `200 OK` → `List<JobApplicationSeekerResponse>` | `403` role |
| `GET /api/jobs/{jobId}/applications` | bearer, `EMPLOYER` (staff) | — | `200 OK` → `List<JobApplicationCompanyResponse>` | `403` not staff of this job's company · `404` |
| `PUT /api/job-applications/{id}/review` | bearer, `EMPLOYER` (staff) | — | `200 OK` → `JobApplicationCompanyResponse` | `403` · `404` · `409` not currently `PENDING` |
| `PUT /api/job-applications/{id}/interview` | bearer, `EMPLOYER` (staff) | — | `200 OK` → `JobApplicationCompanyResponse` | `403` · `404` · `409` not currently `REVIEWED` |
| `PUT /api/job-applications/{id}/accept` | bearer, `EMPLOYER` (staff) | — | `200 OK` → `JobApplicationCompanyResponse` | `403` · `404` · `409` not currently `INTERVIEW` |
| `PUT /api/job-applications/{id}/reject` | bearer, `EMPLOYER` (staff) | — | `200 OK` → `JobApplicationCompanyResponse` | `403` · `404` · `409` already terminal |
| `DELETE /api/job-applications/{id}` | bearer, `JOB_SEEKER` | — | `204 No Content` | `403` not the applicant · `404` |

### Resumes

| Endpoint | Auth | Request Body | Success | Errors |
|---|---|---|---|---|
| `POST /api/resumes` | bearer, `JOB_SEEKER` | multipart `file` | `200 OK` → `ResumeResponse` *(note: not `201 Created`)* | `400` bad/missing file · `403` role |
| `GET /api/resumes` | bearer, `JOB_SEEKER` | — | `200 OK` → `List<ResumeResponse>` | `403` role |
| `GET /api/resumes/{id}/download` | bearer (owner **or** reviewing staff) | — | `200 OK` → binary file stream | `403` neither owner nor reviewing staff · `404` |
| `DELETE /api/resumes/{id}` | bearer, `JOB_SEEKER` (owner only) | — | `204 No Content` | `403` not owner · `404` |

---

## 8. Exception → HTTP Status Mapping

All errors return a consistent `ErrorResponse` shape:
```json
{
  "message": "...",
  "status": 404,
  "timestamp": "2026-09-08T10:00:00",
  "path": "/api/...",
  "errors": null
}
```
`errors` is populated (list of strings) only for validation failures; `null` otherwise.

| Exception | Status | Trigger |
|---|---|---|
| `ResourceNotFoundException` | `404` | Entity not found by ID |
| `ConflictException` | `409` | Business-rule state conflict (duplicate, already processed, wrong status for transition) |
| `ForbiddenException` | `403` | Custom authorization failure (ownership/staff checks) |
| `MethodArgumentNotValidException` | `400` | Bean Validation failure (`@NotBlank`, `@NotNull`, etc.) — `errors` array populated |
| `AccessDeniedException` (Spring Security) | `403` | `@PreAuthorize` role check failure |
| `Exception` (catch-all) | `500` | Any unhandled exception — generic message, real cause logged server-side only |

**⚠️ Still missing:** no dedicated handler for `DataIntegrityViolationException` (e.g. duplicate
email at registration) — this currently falls through to the generic `500` handler instead of a
proper `409 Conflict`. Recommended fix:
```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request){
    ErrorResponse response = new ErrorResponse(
            "A record with these details already exists",
            HttpStatus.CONFLICT.value(), LocalDateTime.now(), request.getRequestURI());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
}
```

**⚠️ Minor cleanup still open:** the handler methods for `ConflictException` and
`ForbiddenException` are both still named `handleResourceNotFoundException` (copy-paste
naming from the `ResourceNotFoundException` handler). Functionally correct (Java resolves by
parameter type), but should be renamed to `handleConflict` / `handleForbidden` respectively
for clarity.

---

## 9. Frontend Behavior by User State

*(Unchanged from prior version.)*

### JOB_SEEKER
**Navigation:** Jobs · My Applications · My Resumes · Profile · Logout
**Dashboard:** Recommended/recent jobs · application statistics · recent applications

### EMPLOYER — without a company
**Navigation:** Jobs · Companies · My Join Requests · Profile · Logout
**Dashboard:** Search companies · Create company · View my join requests
**Join request status display** (`GET /api/join-requests/me`): `PENDING` → "Pending", `APPROVED` → "Approved", `REJECTED` → "Rejected"

### EMPLOYER — with a company
**Navigation:** Dashboard · My Company · My Jobs · Applications · Join Requests · Profile · Logout
- **Owner:** show Join Requests inbox, allow approve/reject
- **Staff:** do not show join-request management in the UI; if attempted anyway, API returns `403` — handle gracefully

*(Admin behavior intentionally omitted.)*

---

## 10. Notes for the Frontend Generator

- Derive owner/staff/no-company state from `/api/me` only — **contingent on `ownerId` being
  present on the nested `company` object** (see section 4 warning; verify before building
  this logic in).
- Treat every `403` as expected and handleable, not necessarily a bug.
- Application status badges need five states: `PENDING`, `REVIEWED`, `INTERVIEW`, `ACCEPTED`,
  `REJECTED` — `REJECTED` should read as a plain outcome, not an error.
- Join request status badges need three states: `PENDING`, `APPROVED`, `REJECTED`.
- Do **not** wire a frontend flow to `GET /api/jobs/{jobId}/applicants` — it is currently a
  dead stub. Use `GET /api/jobs/{jobId}/applications` for the applicant-review screen.
- Registration form must restrict the role selector to `JOB_SEEKER` / `EMPLOYER` only.

---

## 11. Project Requirements

- Responsive desktop/mobile UI
- Component-based architecture
- API calls isolated in a dedicated service layer — no `axios`/`fetch` calls directly inside
  components
- Authentication state (tokens, current user, role, company state) stored globally, not
  per-component
- Protected routes — unauthenticated users redirected to `/login`
- Role-aware navigation — nav items shown/hidden per role and per company-membership state
  (see section 9), while still relying on the backend as the actual authorization boundary
- Loading states for every async view (skeletons or spinners — not a blank screen)
- Empty states for every list view (e.g. "no applications yet", "no jobs match your filters")
- Error states for every async view (distinguish network failure, `403`, `404`, and validation
  failure — don't collapse all errors into one generic message)
- Form validation on the client mirroring the backend's Bean Validation constraints (see
  section 6's DTO tables for exact field constraints), so invalid input is caught before a
  round trip, while the backend remains the authoritative validator
- Toast notifications for action outcomes (apply, approve, reject, upload, delete, etc.)
- Confirmation dialogs before destructive actions (delete resume, delete company, withdraw
  application, reject a join request/application)

## 12. Do Not Infer

The frontend must **not**:
- invent API endpoints not listed in section 7
- invent response fields not present in the DTO tables in section 6
- use the JWT payload to determine company membership or ownership — always use `/api/me`
  (section 4)
- determine ownership by probing a `403` response — derive it from `/api/me`'s `ownerId`
  field instead (sections 4–5)
- allow `ADMIN` as a registration role option (section 1)
- send `companyId` when creating or updating a job — it's derived server-side from the
  authenticated user (section 6, `CreateJobRequest`/`UpdateJobRequest`)
- upload a new resume file as part of application submission — applying references an
  existing `resumeId` from the user's resume library (section 2, `JobApplicationRequest`)
- wire any flow to `GET /api/jobs/{jobId}/applicants` — it is a dead stub; use
  `GET /api/jobs/{jobId}/applications` instead (section 7, section 10)

## 13. Frontend Tech Stack

Build the frontend using:
- React
- TypeScript
- Vite
- React Router
- Axios
- TanStack Query
- Tailwind CSS
- shadcn/ui


## 14. Frontend Project Structure

The React frontend is part of the same Git repository as the Spring Boot backend,
but frontend source code must remain separate from the backend source code.

Recommended structure:

job-portal/
├── src/
│   └── main/
│       ├── java/                 # Spring Boot backend
│       └── resources/
│           └── static/           # Production frontend build output
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── layouts/
│   │   ├── pages/
│   │   ├── hooks/
│   │   ├── types/
│   │   ├── lib/
│   │   └── App.tsx
│   ├── package.json
│   ├── vite.config.ts
│   └── ...
│
├── pom.xml
└── README.md

Do not place React source files directly inside
src/main/java or src/main/resources.

During production build, Vite should generate the frontend static assets,
which are then served by Spring Boot.