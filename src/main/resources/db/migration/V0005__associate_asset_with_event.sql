ALTER TABLE asset ADD COLUMN event_id BINARY(16) DEFAULT NULL;
ALTER TABLE asset ADD FOREIGN KEY asset_event_fk (event_id) REFERENCES event (id);