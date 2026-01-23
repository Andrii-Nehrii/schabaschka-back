package schabaschka.chat.dto;

public class NewInvitationMessageDto {

    private String text;

    public NewInvitationMessageDto() {
    }

    public NewInvitationMessageDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
