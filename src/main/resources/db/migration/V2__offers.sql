

CREATE TABLE offers (
                        id         BIGSERIAL PRIMARY KEY,
                        job_id     BIGINT       NOT NULL,
                        worker_id  BIGINT       NOT NULL,
                        status     VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                        message    TEXT,
                        created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);


ALTER TABLE offers
    ADD CONSTRAINT fk_offers_job
        FOREIGN KEY (job_id) REFERENCES jobs(id);


ALTER TABLE offers
    ADD CONSTRAINT fk_offers_worker
        FOREIGN KEY (worker_id) REFERENCES users(id);


CREATE INDEX idx_offers_job_id     ON offers(job_id);
CREATE INDEX idx_offers_worker_id  ON offers(worker_id);
CREATE INDEX idx_offers_status     ON offers(status);
CREATE INDEX idx_offers_created_at ON offers(created_at);
CREATE INDEX idx_offers_job_status ON offers(job_id, status);
