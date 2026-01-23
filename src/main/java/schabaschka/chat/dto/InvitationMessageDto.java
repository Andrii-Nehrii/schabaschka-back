package schabaschka.chat.dto;

import java.time.OffsetDateTime;

public class InvitationMessageDto {

    private Long id;
    private Long invitationId;
    private Long senderId;
    private String text;
    private OffsetDateTime createdAt;

    public InvitationMessageDto() {
    }

    public InvitationMessageDto(Long id, Long invitationId, Long senderId, String text, OffsetDateTime createdAt) {
        this.id = id;
        this.invitationId = invitationId;
        this.senderId = senderId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getInvitationId() {
        return invitationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInvitationId(Long invitationId) {
        this.invitationId = invitationId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
