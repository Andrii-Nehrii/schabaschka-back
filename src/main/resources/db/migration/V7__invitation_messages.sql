CREATE TABLE invitation_messages (
                                     id            BIGSERIAL PRIMARY KEY,
                                     invitation_id BIGINT      NOT NULL,
                                     sender_id     BIGINT      NOT NULL,
                                     text          TEXT        NOT NULL,
                                     created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE invitation_messages
    ADD CONSTRAINT fk_invitation_messages_invitation
        FOREIGN KEY (invitation_id)
            REFERENCES invitations(id)
            ON DELETE CASCADE;

ALTER TABLE invitation_messages
    ADD CONSTRAINT fk_invitation_messages_sender
        FOREIGN KEY (sender_id) REFERENCES users(id);

CREATE INDEX idx_invitation_messages_invitation_created_at
    ON invitation_messages(invitation_id, created_at);

CREATE INDEX idx_invitation_messages_sender_id
    ON invitation_messages(sender_id);
