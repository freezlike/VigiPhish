CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    display_name VARCHAR(200) NOT NULL,
    role VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE awareness_groups (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE campaigns (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    status VARCHAR(64) NOT NULL,
    owner_id UUID REFERENCES app_users(id),
    validator_id UUID REFERENCES app_users(id),
    internal_domain_allowlist TEXT NOT NULL,
    validation_required BOOLEAN NOT NULL DEFAULT TRUE,
    validated_at TIMESTAMPTZ,
    scheduled_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT campaigns_status_check CHECK (status IN ('DRAFT', 'PENDING_VALIDATION', 'VALIDATED', 'SCHEDULED', 'RUNNING', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE email_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    educational_context TEXT NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE landing_pages (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    educational_message TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE quizzes (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tracking_tokens (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id),
    user_id UUID NOT NULL REFERENCES app_users(id),
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE campaign_events (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id),
    user_id UUID REFERENCES app_users(id),
    event_type VARCHAR(64) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    CONSTRAINT campaign_events_type_check CHECK (event_type IN ('EMAIL_SENT', 'EMAIL_OPENED', 'LINK_CLICKED', 'SUBMITTED_FORM', 'TRAINING_VIEWED', 'QUIZ_COMPLETED'))
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_id UUID REFERENCES app_users(id),
    action VARCHAR(160) NOT NULL,
    target_type VARCHAR(120) NOT NULL,
    target_id UUID,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    details JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_campaign_events_campaign ON campaign_events(campaign_id);
CREATE INDEX idx_campaign_events_user ON campaign_events(user_id);
CREATE INDEX idx_tracking_tokens_campaign ON tracking_tokens(campaign_id);
CREATE INDEX idx_audit_logs_occurred_at ON audit_logs(occurred_at);
