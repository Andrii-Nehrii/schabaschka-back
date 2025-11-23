package schabaschka.chat.dto;

import java.time.OffsetDateTime;

public class OfferMessageDto {
    private Long id;
    private Long offerId;
    private Long senderId;
    private String text;
    private OffsetDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OfferMessageDto() {
    }

    public OfferMessageDto(Long id, Long offerId, Long senderId, String text, OffsetDateTime createdAt) {
        this.id = id;
        this.offerId = offerId;
        this.senderId = senderId;
        this.text = text;
        this.createdAt = createdAt;
    }
}
