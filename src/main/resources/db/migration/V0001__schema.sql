CREATE TABLE asset (
  id BINARY(16) NOT NULL,
  name CHARACTER VARYING(255),
  description CHARACTER VARYING(255),
  PRIMARY KEY (id)
);

CREATE TABLE audit_log (
  id BINARY(16) NOT NULL,
  timestamp BIGINT,
  username CHARACTER VARYING(255),
  remote_address CHARACTER VARYING(255),
  message CHARACTER VARYING(255),
  PRIMARY KEY (id)
);

CREATE TABLE participant (
  id BINARY(16) NOT NULL,
  first_name CHARACTER VARYING(255),
  last_name CHARACTER VARYING(255),
  email CHARACTER VARYING(255),
  PRIMARY KEY(id)
);

CREATE TABLE staff (
  id BINARY(16) NOT NULL,
  username CHARACTER VARYING(255),
  password CHARACTER VARYING(255),
  PRIMARY KEY (id)
);

CREATE TABLE staff_permissions (
  staff_id BINARY(16) NOT NULL,
  permissions CHARACTER VARYING(255),
  PRIMARY KEY (staff_id, permissions),
  CONSTRAINT fk_staff_id FOREIGN KEY (staff_id) REFERENCES staff (id)
);