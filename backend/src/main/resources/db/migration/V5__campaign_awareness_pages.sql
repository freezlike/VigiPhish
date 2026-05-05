ALTER TABLE campaigns
    ADD COLUMN landing_page_id UUID REFERENCES landing_pages(id) ON DELETE SET NULL;

ALTER TABLE campaign_events
    DROP CONSTRAINT campaign_events_type_check,
    ADD CONSTRAINT campaign_events_type_check CHECK (event_type IN (
        'EMAIL_SENT',
        'EMAIL_OPENED',
        'LINK_CLICKED',
        'SUBMITTED_FORM',
        'TRAINING_VIEWED',
        'TRAINING_COMPLETED',
        'QUIZ_COMPLETED'
    ));

UPDATE campaigns
SET landing_page_id = '00000000-0000-0000-0000-000000000401'
WHERE id = '00000000-0000-0000-0000-000000000601'
  AND landing_page_id IS NULL;
