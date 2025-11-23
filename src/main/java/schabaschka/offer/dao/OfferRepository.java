package schabaschka.offer.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import schabaschka.offer.model.Offer;

import java.util.Optional;
import java.util.stream.Stream;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    Stream<Offer> findAllBy();

    Stream<Offer> findByJobId(Long jobId);

    Stream<Offer> findByWorkerId(Long workerId);

    Optional<Offer> findById(long id);



}
