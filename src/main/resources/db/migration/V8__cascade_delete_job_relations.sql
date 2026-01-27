ALTER TABLE offer_messages DROP CONSTRAINT fk_offer_messages_offer;
ALTER TABLE invitation_messages DROP CONSTRAINT fk_invitation_messages_invitation;
ALTER TABLE offers DROP CONSTRAINT fk_offers_job;
ALTER TABLE invitations DROP CONSTRAINT fk_invitations_job;

ALTER TABLE offers
    ADD CONSTRAINT fk_offers_job
        FOREIGN KEY (job_id) REFERENCES jobs(id)
            ON DELETE CASCADE;

ALTER TABLE invitations
    ADD CONSTRAINT fk_invitations_job
        FOREIGN KEY (job_id) REFERENCES jobs(id)
            ON DELETE CASCADE;

ALTER TABLE offer_messages
    ADD CONSTRAINT fk_offer_messages_offer
        FOREIGN KEY (offer_id) REFERENCES offers(id)
            ON DELETE CASCADE;

ALTER TABLE invitation_messages
    ADD CONSTRAINT fk_invitation_messages_invitation
        FOREIGN KEY (invitation_id) REFERENCES invitations(id)
            ON DELETE CASCADE;
