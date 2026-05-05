export type UserRole =
  | 'ROLE_DSSI_ADMIN'
  | 'ROLE_CAMPAIGN_MANAGER'
  | 'ROLE_CAMPAIGN_VALIDATOR'
  | 'ROLE_REPORT_VIEWER'
  | 'ROLE_AUDITOR'
  | 'ROLE_USER';

export type CampaignStatus =
  | 'DRAFT'
  | 'PENDING_VALIDATION'
  | 'VALIDATED'
  | 'SCHEDULED'
  | 'RUNNING'
  | 'COMPLETED'
  | 'CANCELLED';

export type TrackingEventType =
  | 'EMAIL_SENT'
  | 'EMAIL_OPENED'
  | 'LINK_CLICKED'
  | 'SUBMITTED_FORM'
  | 'TRAINING_VIEWED'
  | 'TRAINING_COMPLETED'
  | 'QUIZ_COMPLETED';

export interface User {
  id: string;
  email: string;
  displayName: string;
  role: UserRole;
  active: boolean;
}

export interface UserRequest {
  email: string;
  displayName: string;
  role: UserRole;
  active: boolean;
}

export interface Campaign {
  id: string;
  name: string;
  status: CampaignStatus;
  ownerId?: string | null;
  validatorId?: string | null;
  landingPageId?: string | null;
  internalDomainAllowlist: string;
  validationRequired: boolean;
  validatedAt?: string | null;
  scheduledAt?: string | null;
}

export interface CampaignRequest {
  name: string;
  ownerId?: string | null;
  landingPageId?: string | null;
  internalDomainAllowlist: string;
  validationRequired: boolean;
}

export interface EmailTemplate {
  id: string;
  name: string;
  subject: string;
  educationalContext: string;
  body: string;
}

export type EmailTemplateRequest = Omit<EmailTemplate, 'id'>;

export interface LandingPage {
  id: string;
  name: string;
  educationalMessage: string;
  content: string;
}

export type LandingPageRequest = Omit<LandingPage, 'id'>;

export interface Quiz {
  id: string;
  name: string;
  description: string;
}

export type QuizRequest = Omit<Quiz, 'id'>;

export interface CampaignTarget {
  id: string;
  campaignId: string;
  userId: string;
  expiresAt: string;
  lastEventAt?: string | null;
}

export interface CampaignTargetCreated {
  target: CampaignTarget;
  rawToken: string;
}

export interface CampaignReport {
  campaignId: string;
  targets: number;
  events: Partial<Record<TrackingEventType, number>>;
}

export interface AuditLog {
  id: string;
  actorId?: string | null;
  action: string;
  targetType: string;
  targetId?: string | null;
  occurredAt: string;
  details: Record<string, unknown>;
}

export interface PublicTrackingEventRequest {
  token: string;
  eventType: Exclude<TrackingEventType, 'EMAIL_SENT'>;
}

export interface PublicTrackingEventResponse {
  status: string;
}

export interface PublicAwarenessPage {
  title: string;
  educationalMessage: string;
  content: string;
}
