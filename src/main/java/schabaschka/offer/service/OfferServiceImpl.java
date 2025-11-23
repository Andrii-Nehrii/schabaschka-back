package schabaschka.offer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.offer.dao.OfferRepository;
import schabaschka.offer.dto.OfferDto;
import schabaschka.offer.model.Offer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;

    public OfferServiceImpl(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }




    @Override
    public List<OfferDto> findByJobId(long jobId) {
        try(Stream<Offer> stream = offerRepository.findByJobId(jobId)){
            return stream.map(this::toDto).collect(Collectors.toList());
        }
    }



    @Override
    public List<OfferDto> findByWorkerId(long workerId) {
       try(Stream<Offer> stream = offerRepository.findByWorkerId(workerId)){
           return stream.map(this::toDto).collect(Collectors.toList());
       }
    }

    @Override
    public Optional<OfferDto> findById(long id) {
        return offerRepository.findById(id).map(this::toDto);
    }

    @Override
    @Transactional
    public OfferDto create(long workerId, long jobId, String message) {

        try(Stream<Offer> stream = offerRepository.findByJobId(jobId)){
            boolean alreadyExists = stream.anyMatch(offer -> {
                Long existingWorkerId = offer.getWorkerId();
                return existingWorkerId != null && existingWorkerId == workerId;
            });
            if(alreadyExists){
                throw new IllegalArgumentException( "Offer for jobId " + jobId + " and workerId " + workerId + " already exists");
            }
        }


        Offer offer = new Offer();
        offer.setWorkerId(workerId);
        offer.setJobId(jobId);
        offer.setMessage(message);
        Offer saved = offerRepository.save(offer);
        return toDto(saved);
    }

    @Override
    @Transactional
    public OfferDto changeStatus(Long id, Offer.Status newStatus) {
        if(newStatus ==null){
            throw new IllegalArgumentException("newStatus can't be null");
        }
        Offer offer = offerRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("offer with id " + id + " not found"));
        offer.setStatus(newStatus);
        Offer saved = offerRepository.save(offer);
        return toDto(saved);
    }

    @Override
    public void closePendingOffersForJob(long jobId) {
        try(Stream<Offer> stream = offerRepository.findByJobId(jobId)){
            List<Offer> toUpdate = stream.filter(offer -> offer.getStatus() ==Offer.Status.PENDING).peek(offer -> offer.setStatus((Offer.Status.REJECTED))).collect(Collectors.toList());

            if (!toUpdate.isEmpty()) {
                offerRepository.saveAll(toUpdate);
            }
        }
    }


    private OfferDto toDto(Offer offer) {
        if (offer == null) {
            return null;
        }

        OfferDto dto = new OfferDto();
        dto.setId(offer.getId());
        dto.setJobId(offer.getJobId());
        dto.setWorkerId(offer.getWorkerId());
        dto.setStatus(offer.getStatus());
        dto.setMessage(offer.getMessage());
        dto.setCreatedAt(offer.getCreatedAt());

        return dto;
    }


}
