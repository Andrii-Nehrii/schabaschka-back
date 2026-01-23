CREATE TABLE invitations (
                             id          BIGSERIAL PRIMARY KEY,
                             job_id      BIGINT      NOT NULL,
                             worker_id   BIGINT      NOT NULL,
                             employer_id BIGINT      NOT NULL,
                             status      VARCHAR(32) NOT NULL,
                             message     TEXT,
                             created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE invitations
    ADD CONSTRAINT fk_invitations_job
        FOREIGN KEY (job_id)
            REFERENCES jobs (id)
            ON DELETE CASCADE;

ALTER TABLE invitations
    ADD CONSTRAINT fk_invitations_worker
        FOREIGN KEY (worker_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

ALTER TABLE invitations
    ADD CONSTRAINT fk_invitations_employer
        FOREIGN KEY (employer_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

CREATE INDEX idx_invitations_worker_id
    ON invitations (worker_id);

CREATE INDEX idx_invitations_employer_id
    ON invitations (employer_id);

CREATE INDEX idx_invitations_job_id
    ON invitations (job_id);

CREATE INDEX idx_invitations_status
    ON invitations (status);
