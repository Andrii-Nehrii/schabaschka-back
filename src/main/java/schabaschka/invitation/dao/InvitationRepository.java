package schabaschka.invitation.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import schabaschka.invitation.model.Invitation;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findAllBy();

    List<Invitation> findByWorkerId(long workerId);

    List<Invitation> findByEmployerId(long employerId);

    List<Invitation> findByJobId(long jobId);

    Optional<Invitation> findById(long id);

}
