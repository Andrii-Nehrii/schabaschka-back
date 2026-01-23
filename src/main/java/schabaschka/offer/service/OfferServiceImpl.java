package schabaschka.offer.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.invitation.dao.InvitationRepository;
import schabaschka.invitation.model.Invitation;
import schabaschka.job.JobStatus;
import schabaschka.job.dao.JobRepository;
import schabaschka.job.model.Job;
import schabaschka.offer.dao.OfferRepository;
import schabaschka.offer.dto.OfferDto;
import schabaschka.offer.model.Offer;
import schabaschka.security.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final JobRepository jobRepository;
    private final InvitationRepository invitationRepository;

    public OfferServiceImpl(OfferRepository offerRepository, JobRepository jobRepository, InvitationRepository invitationRepository) {
        this.offerRepository = offerRepository;
        this.jobRepository = jobRepository;
        this.invitationRepository = invitationRepository;
    }

    @Override
    public List<OfferDto> findByJobId(long jobId) {
        SecurityUtils.requireRole("EMPLOYER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
        Long employerId = job.getEmployerId();
        if (employerId == null) {
            throw new AccessDeniedException("Forbidden");
        }
        if (employerId != currentUserId) {
            throw new AccessDeniedException("Forbidden");
        }

        try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
            return stream.map(this::toDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<OfferDto> findByWorkerId(long workerId) {
        SecurityUtils.requireRole("WORKER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != workerId) {
            throw new AccessDeniedException("Worker id not found");
        }
        try (Stream<Offer> stream = offerRepository.findByWorkerId(workerId)) {
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
        SecurityUtils.requireRole("WORKER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != workerId) {
            throw new AccessDeniedException("Forbidden");
        }
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new AccessDeniedException("Job with id  " + jobId + " not open");
        }

        try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
            boolean alreadyExists = stream.anyMatch(offer -> {
                Long existingWorkerId = offer.getWorkerId();
                return existingWorkerId != null && existingWorkerId == workerId;
            });
            if (alreadyExists) {
                throw new IllegalArgumentException("Offer for jobId " + jobId + " and workerId " + workerId + " already exists");
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
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus can't be null");
        }
        if (newStatus == Offer.Status.PENDING) {
            throw new AccessDeniedException("Pending offers can't be changed manually");
        }

        Offer offer = offerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("offer with id " + id + " not found"));

        if (offer.getStatus() != Offer.Status.PENDING) {
            throw new IllegalArgumentException("Only pending offers can be changed");
        }
        long currentUserId = SecurityUtils.getCurrentUserId();

        if (newStatus == Offer.Status.CANCELED) {
            SecurityUtils.requireRole("WORKER");
            Long workerId = offer.getWorkerId();
            if (workerId == null || workerId != currentUserId) {
                throw new AccessDeniedException("Forbidden");
            }
        } else if (newStatus == Offer.Status.ACCEPTED || newStatus == Offer.Status.REJECTED) {
            SecurityUtils.requireRole("EMPLOYER");
            Long jobId = offer.getJobId();
            if (jobId == null) {
                throw new IllegalArgumentException("Offer has no jobId");
            }
            Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("job with id " + jobId + " not found"));
            Long employerId = job.getEmployerId();
            if (employerId == null || employerId != currentUserId) {
                throw new AccessDeniedException("Forbidden");
            }

            if (newStatus == Offer.Status.ACCEPTED) {
                if (job.getStatus() != JobStatus.OPEN) {
                    throw new IllegalArgumentException("Job with id  " + jobId + " not open");
                }

                offer.setStatus(Offer.Status.ACCEPTED);
                Offer saved = offerRepository.save(offer);

                job.setStatus(JobStatus.IN_PROGRESS);
                jobRepository.save(job);

                try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
                    List<Offer> toReject = stream
                            .filter(o -> o.getStatus() == Offer.Status.PENDING)
                            .filter(o -> o.getId() == null || !o.getId().equals(saved.getId()))
                            .peek(o -> o.setStatus(Offer.Status.REJECTED))
                            .collect(Collectors.toList());

                    if (!toReject.isEmpty()) {
                        offerRepository.saveAll(toReject);
                    }
                }

                List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId);
                List<Invitation> toCancel = invitationsForJob.stream()
                        .filter(inv -> inv.getStatus() == Invitation.Status.PENDING)
                        .peek(inv -> inv.setStatus(Invitation.Status.CANCELED))
                        .collect(Collectors.toList());

                if (!toCancel.isEmpty()) {
                    invitationRepository.saveAll(toCancel);
                }

                return toDto(saved);
            }
        } else {
            throw new IllegalArgumentException("Unsupported status: " + newStatus);
        }

        offer.setStatus(newStatus);
        Offer saved = offerRepository.save(offer);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void closePendingOffersForJob(long jobId) {
        try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
            List<Offer> toUpdate = stream
                    .filter(offer -> offer.getStatus() == Offer.Status.PENDING)
                    .peek(offer -> offer.setStatus((Offer.Status.REJECTED)))
                    .collect(Collectors.toList());

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





//package schabaschka.offer.service;
//
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import schabaschka.invitation.dao.InvitationRepository; //changed
//import schabaschka.invitation.model.Invitation; //changed
//import schabaschka.job.JobStatus;
//import schabaschka.job.dao.JobRepository;
//import schabaschka.job.model.Job;
//import schabaschka.offer.dao.OfferRepository;
//import schabaschka.offer.dto.OfferDto;
//import schabaschka.offer.model.Offer;
//import schabaschka.security.SecurityUtils;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//@Transactional(readOnly = true)
//public class OfferServiceImpl implements OfferService {
//    private final OfferRepository offerRepository;
//    private final JobRepository jobRepository;
//    private final InvitationRepository invitationRepository; //changed
//
//    public OfferServiceImpl(OfferRepository offerRepository, JobRepository jobRepository, InvitationRepository invitationRepository) { //changed
//        this.offerRepository = offerRepository;
//        this.jobRepository = jobRepository;
//        this.invitationRepository = invitationRepository; //changed
//    }
//
//    @Override
//    public List<OfferDto> findByJobId(long jobId) {
//        SecurityUtils.requireRole("EMPLOYER");
//        long currentUserId = SecurityUtils.getCurrentUserId();
//        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
//        Long employerId = job.getEmployerId();
//        if (employerId == null) {
//            throw new AccessDeniedException("Forbidden");
//        }
//        if (employerId != currentUserId) {
//            throw new AccessDeniedException("Forbidden");
//        }
//
//        try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
//            return stream.map(this::toDto).collect(Collectors.toList());
//        }
//    }
//
//    @Override
//    public List<OfferDto> findByWorkerId(long workerId) {
//        SecurityUtils.requireRole("WORKER");
//        long currentUserId = SecurityUtils.getCurrentUserId();
//        if (currentUserId != workerId) {
//            throw new AccessDeniedException("Worker id not found");
//        }
//        try (Stream<Offer> stream = offerRepository.findByWorkerId(workerId)) {
//            return stream.map(this::toDto).collect(Collectors.toList());
//        }
//    }
//
//    @Override
//    public Optional<OfferDto> findById(long id) {
//        return offerRepository.findById(id).map(this::toDto);
//    }
//
//    @Override
//    @Transactional
//    public OfferDto create(long workerId, long jobId, String message) {
//        SecurityUtils.requireRole("WORKER");
//        long currentUserId = SecurityUtils.getCurrentUserId();
//        if (currentUserId != workerId) {
//            throw new AccessDeniedException("Forbidden");
//        }
//        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
//
//        if (job.getStatus() != JobStatus.OPEN) {
//            throw new AccessDeniedException("Job with id  " + jobId + " not open");
//        }
//
//        try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
//            boolean alreadyExists = stream.anyMatch(offer -> {
//                Long existingWorkerId = offer.getWorkerId();
//                return existingWorkerId != null && existingWorkerId == workerId;
//            });
//            if (alreadyExists) {
//                throw new IllegalArgumentException("Offer for jobId " + jobId + " and workerId " + workerId + " already exists");
//            }
//        }
//
//        Offer offer = new Offer();
//        offer.setWorkerId(workerId);
//        offer.setJobId(jobId);
//        offer.setMessage(message);
//        Offer saved = offerRepository.save(offer);
//        return toDto(saved);
//    }
//
//    @Override
//    @Transactional
//    public OfferDto changeStatus(Long id, Offer.Status newStatus) {
//        if (newStatus == null) {
//            throw new IllegalArgumentException("newStatus can't be null");
//        }
//        if (newStatus == Offer.Status.PENDING) {
//            throw new AccessDeniedException("Pending offers can't be changed manually");
//        }
//
//        Offer offer = offerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("offer with id " + id + " not found"));
//
//        if (offer.getStatus() != Offer.Status.PENDING) {
//            throw new IllegalArgumentException("Only pending offers can be changed");
//        }
//        long currentUserId = SecurityUtils.getCurrentUserId();
//
//        if (newStatus == Offer.Status.CANCELED) {
//            SecurityUtils.requireRole("WORKER");
//            Long workerId = offer.getWorkerId();
//            if (workerId == null || workerId != currentUserId) {
//                throw new AccessDeniedException("Forbidden");
//            }
//        } else if (newStatus == Offer.Status.ACCEPTED || newStatus == Offer.Status.REJECTED) {
//            SecurityUtils.requireRole("EMPLOYER");
//            Long jobId = offer.getJobId();
//            if (jobId == null) {
//                throw new IllegalArgumentException("Offer has no jobId");
//            }
//            Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("job with id " + jobId + " not found"));
//            Long employerId = job.getEmployerId();
//            if (employerId == null || employerId != currentUserId) {
//                throw new AccessDeniedException("Forbidden");
//            }
//
//            if (newStatus == Offer.Status.ACCEPTED) {
//                if (job.getStatus() != JobStatus.OPEN) {
//                    throw new IllegalArgumentException("Job with id  " + jobId + " not open");
//                }
//
//                offer.setStatus(Offer.Status.ACCEPTED);
//                Offer saved = offerRepository.save(offer);
//
//                job.setStatus(JobStatus.IN_PROGRESS);
//                jobRepository.save(job);
//
//                try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
//                    List<Offer> toReject = stream
//                            .filter(o -> o.getStatus() == Offer.Status.PENDING) //changed
//                            .peek(o -> o.setStatus(Offer.Status.REJECTED)) //changed
//                            .collect(Collectors.toList()); //changed
//
//                    if (!toReject.isEmpty()) {
//                        offerRepository.saveAll(toReject); //changed
//                    }
//                }
//
//                List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId); //changed
//                List<Invitation> toCancel = invitationsForJob.stream() //changed
//                        .filter(inv -> inv.getStatus() == Invitation.Status.PENDING) //changed
//                        .peek(inv -> inv.setStatus(Invitation.Status.CANCELED)) //changed
//                        .collect(Collectors.toList()); //changed
//
//                if (!toCancel.isEmpty()) {
//                    invitationRepository.saveAll(toCancel); //changed
//                }
//
//                return toDto(saved);
//            }
//
//        } else {
//            throw new IllegalArgumentException("Unsupported status: " + newStatus);
//        }
//
//        offer.setStatus(newStatus);
//        Offer saved = offerRepository.save(offer);
//        return toDto(saved);
//    }
//
//    @Override
//    @Transactional
//    public void closePendingOffersForJob(long jobId) {
//        try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
//            List<Offer> toUpdate = stream
//                    .filter(offer -> offer.getStatus() == Offer.Status.PENDING)
//                    .peek(offer -> offer.setStatus((Offer.Status.REJECTED)))
//                    .collect(Collectors.toList());
//
//            if (!toUpdate.isEmpty()) {
//                offerRepository.saveAll(toUpdate);
//            }
//        }
//    }
//
//    private OfferDto toDto(Offer offer) {
//        if (offer == null) {
//            return null;
//        }
//
//        OfferDto dto = new OfferDto();
//        dto.setId(offer.getId());
//        dto.setJobId(offer.getJobId());
//        dto.setWorkerId(offer.getWorkerId());
//        dto.setStatus(offer.getStatus());
//        dto.setMessage(offer.getMessage());
//        dto.setCreatedAt(offer.getCreatedAt());
//
//        return dto;
//    }
//}
