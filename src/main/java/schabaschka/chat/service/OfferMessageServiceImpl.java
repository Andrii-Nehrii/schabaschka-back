package schabaschka.chat.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.chat.dao.OfferMessageRepository;
import schabaschka.chat.dto.NewOfferMessageDto;
import schabaschka.chat.dto.OfferMessageDto;
import schabaschka.chat.model.OfferMessage;
import schabaschka.job.dao.JobRepository;
import schabaschka.job.model.Job;
import schabaschka.offer.dao.OfferRepository;
import schabaschka.offer.model.Offer;
import schabaschka.security.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OfferMessageServiceImpl implements OfferMessageService {
    private final OfferMessageRepository offerMessageRepository;
    private final JobRepository jobRepository;
    private  final OfferRepository offerRepository;


    public OfferMessageServiceImpl(OfferMessageRepository offerMessageRepository, JobRepository jobRepository, OfferRepository offerRepository) {
        this.offerMessageRepository = offerMessageRepository;
        this.jobRepository = jobRepository;
        this.offerRepository = offerRepository;
    }


    @Override
    public List<OfferMessageDto> getMessageByOfferId(Long offerId) {
        if (offerId == null) {
            throw new IllegalArgumentException("offerId is null");
        }

        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new IllegalArgumentException("offer id not found " + offerId ));
        if(offer.getJobId() == null){
            throw new IllegalArgumentException("jobId is null");
        }
        Job job = jobRepository.findById(offer.getJobId()).orElseThrow(() -> new IllegalArgumentException("job id not found " + offer.getJobId()));
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isWorker = currentUserId.equals(offer.getWorkerId());
        boolean isEmployer = currentUserId.equals(job.getEmployerId());

        if(!isWorker && !isEmployer){
            throw new AccessDeniedException("Forbidden");
        }

        List<OfferMessage> messages = offerMessageRepository.findByOfferIdOrderByCreatedAtAsc(offerId);

        return messages.stream().map(this::toDto).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public OfferMessageDto addMessage(Long offerId, NewOfferMessageDto newOfferMessageDto) {
        if (offerId == null) {
            throw new IllegalArgumentException("offerId must not be null");
        }
        if (newOfferMessageDto == null) {
            throw new IllegalArgumentException("newOfferMessageDto must not be null");
        }
        if(newOfferMessageDto.getText() == null || newOfferMessageDto.getText().isEmpty()){
            throw new IllegalArgumentException("text must not be empty");
        }

        Offer offer = offerRepository.findById(offerId).orElseThrow(()-> new IllegalArgumentException("Offer not found: " + offerId));
        if(offer.getJobId() == null){
            throw new IllegalArgumentException("jobId must not be null");
        }
        Job job = jobRepository.findById(offer.getJobId()).orElseThrow(()->  new IllegalArgumentException("Job not found for offer: " + offer.getJobId()));


        Long senderId = SecurityUtils.getCurrentUserId();



        boolean isWorker = senderId.equals(offer.getWorkerId());
        boolean isEmployer = senderId.equals(job.getEmployerId());

        if (!isWorker && !isEmployer) {
            throw new AccessDeniedException("Forbidden");

        }


        OfferMessage message = new OfferMessage();
        message.setOfferId(offerId);
        message.setSenderId(senderId);
        message.setText(newOfferMessageDto.getText());
        OfferMessage saved = offerMessageRepository.save(message);
        return toDto(saved);
    }




    private OfferMessageDto toDto(OfferMessage message) {
        OfferMessageDto dto = new OfferMessageDto();
        dto.setId(message.getId());
        dto.setOfferId(message.getOfferId());
        dto.setSenderId(message.getSenderId());
        dto.setText(message.getText());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }




}

