package schabaschka.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import schabaschka.chat.model.InvitationMessage;

import java.util.List;

public interface InvitationMessageRepository extends JpaRepository<InvitationMessage, Long> {

    List<InvitationMessage> findByInvitationIdOrderByCreatedAtAsc(Long invitationId);
}
