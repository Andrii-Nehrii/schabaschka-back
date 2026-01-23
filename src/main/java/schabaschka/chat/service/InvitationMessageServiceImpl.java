package schabaschka.chat.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.chat.dao.InvitationMessageRepository;
import schabaschka.chat.dto.InvitationMessageDto;
import schabaschka.chat.dto.NewInvitationMessageDto;
import schabaschka.chat.model.InvitationMessage;
import schabaschka.invitation.dao.InvitationRepository;
import schabaschka.invitation.model.Invitation;
import schabaschka.security.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class InvitationMessageServiceImpl implements InvitationMessageService {

    private final InvitationMessageRepository invitationMessageRepository;
    private final InvitationRepository invitationRepository;

    public InvitationMessageServiceImpl(
            InvitationMessageRepository invitationMessageRepository,
            InvitationRepository invitationRepository
    ) {
        this.invitationMessageRepository = invitationMessageRepository;
        this.invitationRepository = invitationRepository;
    }

    @Override
    public List<InvitationMessageDto> getMessageByInvitationId(Long invitationId) {
        if (invitationId == null) {
            throw new IllegalArgumentException("invitationId is null");
        }

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("invitation id not found " + invitationId));

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isWorker = currentUserId.equals(invitation.getWorkerId());
        boolean isEmployer = currentUserId.equals(invitation.getEmployerId());

        if (!isWorker && !isEmployer) {
            throw new AccessDeniedException("Forbidden");
        }

        List<InvitationMessage> messages = invitationMessageRepository.findByInvitationIdOrderByCreatedAtAsc(invitationId);
        return messages.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvitationMessageDto addMessage(Long invitationId, NewInvitationMessageDto newInvitationMessageDto) {
        if (invitationId == null) {
            throw new IllegalArgumentException("invitationId must not be null");
        }
        if (newInvitationMessageDto == null) {
            throw new IllegalArgumentException("newInvitationMessageDto must not be null");
        }
        if (newInvitationMessageDto.getText() == null || newInvitationMessageDto.getText().isEmpty()) {
            throw new IllegalArgumentException("text must not be empty");
        }

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found: " + invitationId));

        InvitationMessage message = getInvitationMessage(invitationId, newInvitationMessageDto, invitation);

        InvitationMessage saved = invitationMessageRepository.save(message);
        return toDto(saved);
    }

    private static InvitationMessage getInvitationMessage(Long invitationId, NewInvitationMessageDto newInvitationMessageDto, Invitation invitation) {
        Long senderId = SecurityUtils.getCurrentUserId();

        boolean isWorker = senderId.equals(invitation.getWorkerId());
        boolean isEmployer = senderId.equals(invitation.getEmployerId());

        if (!isWorker && !isEmployer) {
            throw new AccessDeniedException("Forbidden");
        }

        InvitationMessage message = new InvitationMessage();
        message.setInvitationId(invitationId);
        message.setSenderId(senderId);
        message.setText(newInvitationMessageDto.getText());
        return message;
    }

    private InvitationMessageDto toDto(InvitationMessage message) {
        InvitationMessageDto dto = new InvitationMessageDto();
        dto.setId(message.getId());
        dto.setInvitationId(message.getInvitationId());
        dto.setSenderId(message.getSenderId());
        dto.setText(message.getText());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
