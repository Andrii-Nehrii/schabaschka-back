package schabaschka.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import schabaschka.chat.dto.InvitationMessageDto;
import schabaschka.chat.dto.NewInvitationMessageDto;
import schabaschka.chat.service.InvitationMessageServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/invitations/{invitationId}/messages")
public class InvitationMessageController {

    private final InvitationMessageServiceImpl invitationMessageService;

    public InvitationMessageController(InvitationMessageServiceImpl invitationMessageService) {
        this.invitationMessageService = invitationMessageService;
    }

    @GetMapping
    public List<InvitationMessageDto> getMessages(@PathVariable("invitationId") long invitationId) {
        return invitationMessageService.getMessageByInvitationId(invitationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationMessageDto createMessage(
            @PathVariable("invitationId") long invitationId,
            @RequestBody NewInvitationMessageDto newInvitationMessageDto
    ) {
        return invitationMessageService.addMessage(invitationId, newInvitationMessageDto);
    }
}
