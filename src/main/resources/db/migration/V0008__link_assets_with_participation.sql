ALTER TABLE event_participants ADD COLUMN asset_id BINARY(16) UNIQUE DEFAULT NULL;
ALTER TABLE event_participants ADD FOREIGN KEY event_participant_asset_fk (asset_id) REFERENCES asset (id);