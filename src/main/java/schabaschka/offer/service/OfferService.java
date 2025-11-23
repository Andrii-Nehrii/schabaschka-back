package schabaschka.offer.service;

import schabaschka.offer.dto.OfferDto;
import schabaschka.offer.model.Offer;


import java.util.List;
import java.util.Optional;

public interface OfferService {

    List<OfferDto> findByJobId(long jobId);

    List<OfferDto> findByWorkerId(long workerId);

    Optional<OfferDto> findById(long id);

    OfferDto create (long workerId, long jobId, String message);

    OfferDto changeStatus(Long id, Offer.Status newStatus);

    void closePendingOffersForJob(long jobId);
}
