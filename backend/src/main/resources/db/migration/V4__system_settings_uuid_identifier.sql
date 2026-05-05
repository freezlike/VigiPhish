ALTER TABLE system_settings
    ADD COLUMN id UUID;

UPDATE system_settings
SET id = gen_random_uuid()
WHERE id IS NULL;

ALTER TABLE system_settings
    ALTER COLUMN id SET NOT NULL,
    ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE system_settings
    DROP CONSTRAINT system_settings_pkey,
    ADD CONSTRAINT system_settings_pkey PRIMARY KEY (id),
    ADD CONSTRAINT system_settings_setting_key_key UNIQUE (setting_key);
