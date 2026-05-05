import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AuditLog,
  Campaign,
  CampaignReport,
  CampaignRequest,
  CampaignTarget,
  CampaignTargetCreated,
  EmailTemplate,
  EmailTemplateRequest,
  LandingPage,
  LandingPageRequest,
  PublicTrackingEventRequest,
  PublicTrackingEventResponse,
  PublicAwarenessPage,
  Quiz,
  QuizRequest,
  User,
  UserRequest
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  listUsers() {
    return this.http.get<User[]>('/api/admin/users');
  }

  createUser(request: UserRequest) {
    return this.http.post<User>('/api/admin/users', request);
  }

  listCampaigns() {
    return this.http.get<Campaign[]>('/api/admin/campaigns');
  }

  getCampaign(id: string) {
    return this.http.get<Campaign>(`/api/admin/campaigns/${id}`);
  }

  createCampaign(request: CampaignRequest) {
    return this.http.post<Campaign>('/api/admin/campaigns', request);
  }

  updateCampaign(id: string, request: CampaignRequest) {
    return this.http.put<Campaign>(`/api/admin/campaigns/${id}`, request);
  }

  submitCampaign(id: string) {
    return this.http.post<Campaign>(`/api/admin/campaigns/${id}/submit-validation`, {});
  }

  validateCampaign(id: string) {
    return this.http.post<Campaign>(`/api/admin/campaigns/${id}/validate`, {});
  }

  scheduleCampaign(id: string, scheduledAt: string) {
    return this.http.post<Campaign>(`/api/admin/campaigns/${id}/schedule`, { scheduledAt });
  }

  startCampaign(id: string) {
    return this.http.post<Campaign>(`/api/admin/campaigns/${id}/start`, {});
  }

  completeCampaign(id: string) {
    return this.http.post<Campaign>(`/api/admin/campaigns/${id}/complete`, {});
  }

  cancelCampaign(id: string) {
    return this.http.post<Campaign>(`/api/admin/campaigns/${id}/cancel`, {});
  }

  listTargets(campaignId: string) {
    return this.http.get<CampaignTarget[]>(`/api/admin/campaigns/${campaignId}/targets`);
  }

  createTarget(campaignId: string, userId: string, expiresAt?: string) {
    return this.http.post<CampaignTargetCreated>(`/api/admin/campaigns/${campaignId}/targets`, { userId, expiresAt });
  }

  listEmailTemplates() {
    return this.http.get<EmailTemplate[]>('/api/admin/email-templates');
  }

  createEmailTemplate(request: EmailTemplateRequest) {
    return this.http.post<EmailTemplate>('/api/admin/email-templates', request);
  }

  updateEmailTemplate(id: string, request: EmailTemplateRequest) {
    return this.http.put<EmailTemplate>(`/api/admin/email-templates/${id}`, request);
  }

  deleteEmailTemplate(id: string) {
    return this.http.delete<void>(`/api/admin/email-templates/${id}`);
  }

  listLandingPages() {
    return this.http.get<LandingPage[]>('/api/admin/landing-pages');
  }

  createLandingPage(request: LandingPageRequest) {
    return this.http.post<LandingPage>('/api/admin/landing-pages', request);
  }

  updateLandingPage(id: string, request: LandingPageRequest) {
    return this.http.put<LandingPage>(`/api/admin/landing-pages/${id}`, request);
  }

  deleteLandingPage(id: string) {
    return this.http.delete<void>(`/api/admin/landing-pages/${id}`);
  }

  listQuizzes() {
    return this.http.get<Quiz[]>('/api/admin/quizzes');
  }

  createQuiz(request: QuizRequest) {
    return this.http.post<Quiz>('/api/admin/quizzes', request);
  }

  updateQuiz(id: string, request: QuizRequest) {
    return this.http.put<Quiz>(`/api/admin/quizzes/${id}`, request);
  }

  deleteQuiz(id: string) {
    return this.http.delete<void>(`/api/admin/quizzes/${id}`);
  }

  getCampaignReport(campaignId: string) {
    return this.http.get<CampaignReport>(`/api/admin/reports/campaigns/${campaignId}/summary`);
  }

  listAuditLogs() {
    return this.http.get<AuditLog[]>('/api/admin/audit-logs');
  }

  recordPublicEvent(request: PublicTrackingEventRequest) {
    return this.http.post<PublicTrackingEventResponse>('/api/public/tracking/events', request);
  }

  getPublicAwarenessPage(token: string) {
    return this.http.get<PublicAwarenessPage>(`/api/public/awareness/${encodeURIComponent(token)}`);
  }
}
