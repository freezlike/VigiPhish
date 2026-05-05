# API

Base path: `/api`.

All admin endpoints require authentication and method-level RBAC. Public endpoints intentionally expose no user identity.

## Health

### `GET /api/health`

Public endpoint for local supervision and frontend status checks.

Response `200 OK`:

```json
{
  "status": "UP",
  "application": "dssi-phishing-awareness",
  "checkedAt": "2026-05-05T10:15:30Z"
}
```

## Users

Path: `/api/admin/users`

Required role: `ROLE_DSSI_ADMIN`.

- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`

Users use UUID identifiers and application roles only. No password or credential field exists in the API.

## User Groups

Path: `/api/admin/user-groups`

Required role: `ROLE_DSSI_ADMIN`.

- `GET /api/admin/user-groups`
- `POST /api/admin/user-groups`
- `PUT /api/admin/user-groups/{id}`
- `DELETE /api/admin/user-groups/{id}`

## Campaigns

Path: `/api/admin/campaigns`

Read roles: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_MANAGER`, `ROLE_CAMPAIGN_VALIDATOR`, `ROLE_REPORT_VIEWER`.
Mutation roles: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_MANAGER`.
Validation role: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_VALIDATOR`.

- `GET /api/admin/campaigns`
- `GET /api/admin/campaigns/{id}`
- `POST /api/admin/campaigns`
- `PUT /api/admin/campaigns/{id}`
- `POST /api/admin/campaigns/{id}/submit-validation`
- `POST /api/admin/campaigns/{id}/validate`
- `POST /api/admin/campaigns/{id}/schedule`
- `POST /api/admin/campaigns/{id}/start`
- `POST /api/admin/campaigns/{id}/complete`
- `POST /api/admin/campaigns/{id}/cancel`

Lifecycle:

```text
DRAFT -> PENDING_VALIDATION -> VALIDATED -> SCHEDULED -> RUNNING -> COMPLETED
```

`CANCELLED` is allowed before `COMPLETED` and before a campaign is already cancelled.

## Email Templates

Path: `/api/admin/email-templates`

Required roles: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_MANAGER`.

- `GET /api/admin/email-templates`
- `POST /api/admin/email-templates`
- `PUT /api/admin/email-templates/{id}`
- `DELETE /api/admin/email-templates/{id}`

Templates must remain educational and internal. They must not implement external spoofing or antispam evasion.

## Landing Pages

Path: `/api/admin/landing-pages`

Required roles: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_MANAGER`.

- `GET /api/admin/landing-pages`
- `POST /api/admin/landing-pages`
- `PUT /api/admin/landing-pages/{id}`
- `DELETE /api/admin/landing-pages/{id}`

Landing pages are pedagogical. Simulated forms must not store submitted field values.

## Quizzes

Path: `/api/admin/quizzes`

Required roles: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_MANAGER`.

- `GET /api/admin/quizzes`
- `POST /api/admin/quizzes`
- `PUT /api/admin/quizzes/{id}`
- `DELETE /api/admin/quizzes/{id}`

## Campaign Targets

Path: `/api/admin/campaigns/{campaignId}/targets`

Required roles: `ROLE_DSSI_ADMIN`, `ROLE_CAMPAIGN_MANAGER`.

- `GET /api/admin/campaigns/{campaignId}/targets`
- `POST /api/admin/campaigns/{campaignId}/targets`

Creating a target generates a long random token and returns the raw token only in that creation response. The database stores only `token_hash`; tokens are expirable via `expiresAt`.

## Public Tracking

Path: `/api/public/tracking`

- `POST /api/public/tracking/events`

Allowed public event types:

- `EMAIL_OPENED`
- `LINK_CLICKED`
- `SUBMITTED_FORM`
- `TRAINING_VIEWED`
- `QUIZ_COMPLETED`

`EMAIL_SENT` is admin/system-only and is rejected on public tracking endpoints.

Request shape:

```json
{
  "token": "raw-token-from-target-creation",
  "eventType": "LINK_CLICKED"
}
```

For `SUBMITTED_FORM`, the backend records only the event and safe metadata indicating that form content was not stored. Submitted field contents are never persisted.

The response does not expose campaign target identity or user identity.

## Audit Logs

Path: `/api/admin/audit-logs`

Required roles: `ROLE_DSSI_ADMIN`, `ROLE_AUDITOR`.

- `GET /api/admin/audit-logs`

All admin mutations implemented in the backend write an audit log entry.

## Reports

Path: `/api/admin/reports`

Required roles: `ROLE_DSSI_ADMIN`, `ROLE_REPORT_VIEWER`.

- `GET /api/admin/reports/campaigns/{campaignId}/summary`

Reports return campaign-level aggregate counts and event totals. They do not expose public tracking token values or public endpoint user identity.

## System Settings

Path: `/api/admin/system-settings`

Required role: `ROLE_DSSI_ADMIN`.

- `GET /api/admin/system-settings`
- `POST /api/admin/system-settings`
- `DELETE /api/admin/system-settings/{key}`

Settings have a UUID identifier and a unique functional key. They are stored as environment-complementary runtime configuration values and must not contain credentials.

## Errors And Validation

- API inputs use DTOs with Bean Validation annotations.
- Validation failures return `400`.
- Missing resources return `404`.
- Business-rule violations return `400`.
- Unhandled errors return a centralized `500` response without leaking internals.
