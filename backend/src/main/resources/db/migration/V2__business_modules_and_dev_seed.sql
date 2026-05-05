CREATE TABLE IF NOT EXISTS user_group_members (
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    group_id UUID NOT NULL REFERENCES awareness_groups(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, group_id)
);

CREATE TABLE IF NOT EXISTS campaign_targets (
    id UUID PRIMARY KEY,
    campaign_id UUID NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_event_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS system_settings (
    setting_key VARCHAR(120) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    description TEXT,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_campaign_targets_campaign ON campaign_targets(campaign_id);
CREATE INDEX IF NOT EXISTS idx_campaign_targets_user ON campaign_targets(user_id);
CREATE INDEX IF NOT EXISTS idx_campaign_targets_expires_at ON campaign_targets(expires_at);

INSERT INTO app_users (id, email, display_name, role, active, created_at, updated_at)
VALUES
    ('00000000-0000-0000-0000-000000000101', 'admin@example.internal', 'DSSI Admin Dev', 'ROLE_DSSI_ADMIN', TRUE, now(), now()),
    ('00000000-0000-0000-0000-000000000102', 'manager@example.internal', 'Campaign Manager Dev', 'ROLE_CAMPAIGN_MANAGER', TRUE, now(), now()),
    ('00000000-0000-0000-0000-000000000103', 'validator@example.internal', 'Campaign Validator Dev', 'ROLE_CAMPAIGN_VALIDATOR', TRUE, now(), now()),
    ('00000000-0000-0000-0000-000000000104', 'learner@example.internal', 'Learner Dev', 'ROLE_USER', TRUE, now(), now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO awareness_groups (id, name, description, created_at)
VALUES
    ('00000000-0000-0000-0000-000000000201', 'Developpement interne', 'Groupe de donnees locales non nominatives reelles.', now())
ON CONFLICT (name) DO NOTHING;

INSERT INTO user_group_members (user_id, group_id)
VALUES
    ('00000000-0000-0000-0000-000000000104', '00000000-0000-0000-0000-000000000201')
ON CONFLICT DO NOTHING;

INSERT INTO email_templates (id, name, subject, educational_context, body, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000301',
    'Rappel formation securite',
    'Rappel formation securite interne',
    'Modele local pedagogique sans usurpation externe.',
    'Bonjour, une ressource de sensibilisation interne est disponible.',
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO landing_pages (id, name, educational_message, content, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000401',
    'Page de formation locale',
    'Cette page explique les signaux de vigilance et ne collecte aucune donnee sensible.',
    'Contenu pedagogique local.',
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO quizzes (id, name, description, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000501',
    'Quiz vigilance initial',
    'Quiz local de sensibilisation, sans collecte de secrets.',
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO campaigns (id, name, status, owner_id, validator_id, internal_domain_allowlist, validation_required, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000601',
    'Campagne de demonstration locale',
    'DRAFT',
    '00000000-0000-0000-0000-000000000102',
    NULL,
    'example.internal',
    TRUE,
    now(),
    now()
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO system_settings (setting_key, setting_value, description, updated_at)
VALUES
    ('APP.INTERNAL_EMAIL_DOMAINS', 'example.internal', 'Domaines internes autorises en developpement.', now()),
    ('RETENTION.EVENT_DAYS', '180', 'Duree de conservation indicative pour les evenements.', now())
ON CONFLICT (setting_key) DO NOTHING;
