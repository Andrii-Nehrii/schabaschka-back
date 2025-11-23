
CREATE TABLE offer_messages (
                                id         BIGSERIAL PRIMARY KEY,
                                offer_id   BIGINT       NOT NULL,
                                sender_id  BIGINT       NOT NULL,
                                text       TEXT         NOT NULL,
                                created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
ALTER TABLE offer_messages
    ADD CONSTRAINT fk_offer_messages_offer
        FOREIGN KEY (offer_id) REFERENCES offers(id);

ALTER TABLE offer_messages
    ADD CONSTRAINT fk_offer_messages_sender
        FOREIGN KEY (sender_id) REFERENCES users(id);

CREATE INDEX idx_offer_messages_offer_created_at
    ON offer_messages(offer_id, created_at);

CREATE INDEX idx_offer_messages_sender_id
    ON offer_messages(sender_id);
