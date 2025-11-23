package schabaschka.chat.dto;

public class NewOfferMessageDto {

    private Long senderId;
    private String text;

    public NewOfferMessageDto() {
    }

    public NewOfferMessageDto(Long senderId, String text) {
        this.senderId = senderId;
        this.text = text;
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
}
