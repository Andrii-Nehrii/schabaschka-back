package schabaschka.chat.service;

import schabaschka.chat.dto.InvitationMessageDto;
import schabaschka.chat.dto.NewInvitationMessageDto;

import java.util.List;

public interface InvitationMessageService {

    List<InvitationMessageDto> getMessageByInvitationId(Long invitationId);

    InvitationMessageDto addMessage(Long invitationId, NewInvitationMessageDto newInvitationMessageDto);
}
