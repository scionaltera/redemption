CREATE TABLE event_participants (
  participants_id BINARY(16) NOT NULL,
  event_id BINARY(16) NOT NULL,
  PRIMARY KEY (participants_id, event_id),
  FOREIGN KEY participant_id_join_fk (participants_id) REFERENCES participant (id),
  FOREIGN KEY event_id_join_fk (event_id) REFERENCES event (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;