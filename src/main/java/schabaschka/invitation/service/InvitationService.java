package schabaschka.invitation.service;

import schabaschka.invitation.dto.InvitationDto;
import schabaschka.invitation.dto.NewInvitationDto;
import schabaschka.invitation.model.Invitation;


import java.util.List;
import java.util.Optional;

public interface InvitationService {

    List<InvitationDto> findByWorkerId(long workerId);

    List<InvitationDto> findByEmployerId(long employerId);

    Optional<InvitationDto> findById(long id);

    InvitationDto create(NewInvitationDto newInvitationDto);

    InvitationDto changeStatus(long invitationId, Invitation.Status newStatus  );

    void closePendingInvitationsForJob(long jobId);

}
