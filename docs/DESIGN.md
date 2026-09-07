# Hearth — Design & Product Brief

A two-sided hiring platform with company membership/ownership and a multi-stage application
review process — not a generic "post job / apply job" board.

## Business logic to design for

1. **Three user types with different home experiences**
   - `JOB_SEEKER`: browses/searches/filters jobs, uploads resumes to a reusable library,
     applies referencing an existing resume, tracks application status.
   - `EMPLOYER`: either owns a company, is staff at one, or has no company yet. Dashboard
     differs visually based on which state they're in.
   - `ADMIN`: elevated permissions, lower priority for now.

2. **Company ownership vs. staff membership is a real, UI-visible distinction**
   - The OWNER (creator of the company) sees a "Join requests" inbox with approve/reject
     actions — owner-only, never shown to staff.
   - STAFF (approved members, non-owner) can post/edit/delete jobs and review applicants
     for their company, but never see join requests.
   - An employer with no company sees two distinct paths: "Create a company" (become
     owner) OR "Search and request to join an existing company" (become staff pending
     approval) — never collapse these into one generic "get started" flow.

3. **Job applications go through a five-stage state machine, order-enforced**:
   `PENDING → REVIEWED → INTERVIEW → ACCEPTED` (or `→ REJECTED` from most non-terminal states).
   Each status needs a distinct, non-alarming visual treatment — especially `REJECTED`, which
   should read as a plain outcome, not an error state. `INTERVIEW` is a distinct stage from
   `REVIEWED` — don't merge them visually.

4. **Resumes are a reusable library, not a per-application upload**
   - A job seeker uploads resume(s) once, to a personal library.
   - Applying to a job means selecting an EXISTING resume from that library, not uploading
     a new file each time.
   - Resume access is dual-path: the owner always sees it; company staff can view it ONLY
     if it's attached to a real application submitted to their company.

5. **Job application review is separate from company join requests** — two different
   screens, two different approval flows, never merged into one generic "requests" inbox.

## Core screens needed

- Public job search/browse (filterable: experience level, work type [remote/hybrid/
  on-site], employment type [full-time/part-time/contract/internship], location, salary
  range) — top filter bar with popover panels per filter, not a sidebar.
- Job detail page.
- Company profile page (public — shows open jobs, NOT staff roster).
- Employer dashboard: differs by state (no company / pending join request / owner / staff).
- Owner-only: join requests inbox (dense scannable rows, not oversized cards).
- Staff: job posting management + applicant review per job, with resume view access.
- Job seeker dashboard: application tracker across all 5 statuses.
- Resume upload/apply modal with drag-and-drop file upload.
- Login and register screens (register includes a role-picker: job seeker vs. employer).

## Visual system — exact tokens, do not reinterpret

### Colors
- Page background: `#FBF9F4` (warm off-white — never pure white, never gray, never dark mode)
- Card surfaces: `#FFFFFF` with 0.5px border `#E4E2D8`, no drop shadows, no gradients
- Primary text: `#2C2C2A`
- Secondary text: `#5F5E5A`
- Muted text: `#888780`
- Primary accent (buttons, links, brand mark): `#0F6E56` (deep teal-green)
- Accent-on-light background: `#E1F5EE`, accent-on-light text: `#085041`
- Neutral avatar/logo placeholder fill: `#EEECE3`, text: `#5F5E5A`

### Status pill colors (soft fill + matching dark text, never alarming saturation)
| Status | Fill | Text |
|---|---|---|
| `PENDING` | `#F1EFE8` (neutral gray) | `#5F5E5A` |
| `REVIEWED` | `#FAEEDA` (amber) | `#633806` |
| `INTERVIEW` | `#E8EDFA` (muted blue/lavender) | `#2E3F73` |
| `ACCEPTED` | `#E1F5EE` (teal, same family as brand accent) | `#085041` |
| `REJECTED` | `#FAECE7` (soft coral/terracotta, deliberately not alarm-red) | `#712B13` |

### Typography
- Headlines: confident editorial serif, regular weight, ~32px for hero headlines, ~1.3
  line-height. Recommended: **Fraunces** (or Instrument Serif as an alternative).
- All UI chrome (nav, buttons, labels, body): plain grotesk sans, two weights only — 400
  regular and 500 medium, never bold/700. Recommended: **Inter** (or General Sans).
- Sentence case everywhere, no ALL CAPS, no Title Case except proper nouns.

### Component style
- Cards: 12px border radius, hairline 0.5px borders, no shadows, no gradients.
- Buttons: 8px radius, solid dark (`#2C2C2A`) for primary actions, outline/transparent for
  secondary — never colored gradients.
- Status/filter chips: fully rounded pill shape (20px radius), soft fill, no border on
  active state.
- Avatars/logos: rounded-square (9px radius) with initials, not circles, not photos.
- No stock photography, no hero illustration — typography and layout carry the page.
- Dashboards use dense, scannable list rows ("inbox row" style), not oversized cards with
  excess whitespace — especially the join-requests inbox and applicant list.

## Build target
React + Tailwind (or static HTML, specify per screen/tool as needed). Match every screen to
this exact token set so they read as one coherent product, not separately generated pages.


## The application must be fully responsive.

Desktop:
- Dense two-column layouts where appropriate
- Full navigation
- Filter bar displayed horizontally

Tablet:
- Reduce column count and spacing
- Preserve the same visual hierarchy

Mobile:
- Navigation collapses into a mobile menu
- Filter bar becomes horizontally scrollable or opens filters through a filter button
- Tables/lists become stacked rows where necessary
- Modals become near-full-screen sheets
- No horizontal page scrolling
- Preserve the same colors, typography, status treatments, and component language

## Design non-negotiables

These rules apply to every screen and component:

* Do not introduce colors outside the defined token system unless required for accessibility or browser-native states.
* Do not use gradients.
* Do not use drop shadows.
* Do not use pure white as the page background.
* Do not use dark mode.
* Do not use circular avatars; use rounded-square avatars.
* Do not use bold/700 typography.
* Do not use ALL CAPS.
* Do not turn dense operational lists into oversized dashboard cards.
* Do not invent additional product features, screens, filters, statuses, or workflows that are not defined in the API or design references.
* Keep visual hierarchy, spacing, typography, borders, radii, and status treatments consistent across all pages.
* When a backend/API limitation conflicts with the desired UI, do not invent a frontend workaround. Follow the API reference and flag the dependency.
