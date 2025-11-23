package schabaschka.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import schabaschka.chat.model.OfferMessage;
import schabaschka.chat.model.OfferMessage;

import java.util.List;

public interface OfferMessageRepository extends JpaRepository<OfferMessage,Long> {

    List<OfferMessage> findByOfferIdOrderByCreatedAtAsc(Long offerId);
}
