package schabaschka.invitation.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.invitation.dao.InvitationRepository;
import schabaschka.invitation.dto.InvitationDto;
import schabaschka.invitation.dto.NewInvitationDto;
import schabaschka.invitation.model.Invitation;
import schabaschka.job.JobStatus;
import schabaschka.job.dao.JobRepository;
import schabaschka.job.model.Job;
import schabaschka.offer.dao.OfferRepository;
import schabaschka.offer.model.Offer;
import schabaschka.security.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;
    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;

    public InvitationServiceImpl(InvitationRepository invitationRepository, JobRepository jobRepository, OfferRepository offerRepository) {
        this.invitationRepository = invitationRepository;
        this.jobRepository = jobRepository;
        this.offerRepository = offerRepository;
    }

    @Override
    public List<InvitationDto> findByWorkerId(long workerId) {
        SecurityUtils.requireRole("WORKER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != workerId) {
            throw new AccessDeniedException("Forbidden");
        }
        List<Invitation> invitations = invitationRepository.findByWorkerId(workerId);
        return invitations.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<InvitationDto> findByEmployerId(long employerId) {
        SecurityUtils.requireRole("EMPLOYER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != employerId) {
            throw new AccessDeniedException("Forbidden");
        }
        List<Invitation> invitations = invitationRepository.findByEmployerId(employerId);
        return invitations.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<InvitationDto> findById(long id) {
        Optional<Invitation> invitationOpt = invitationRepository.findById(id);
        if (invitationOpt.isEmpty()) {
            return Optional.empty();
        }
        Invitation invitation = invitationOpt.get();
        long currentUserId = SecurityUtils.getCurrentUserId();
        Long workerId = invitation.getWorkerId();
        Long employerId = invitation.getEmployerId();

        if (workerId != null && workerId == currentUserId) {
            SecurityUtils.requireRole("WORKER");
            return Optional.of(toDto(invitation));
        }
        if (employerId != null && employerId == currentUserId) {
            SecurityUtils.requireRole("EMPLOYER");
            return Optional.of(toDto(invitation));
        }
        throw new AccessDeniedException("Forbidden");
    }

    @Override
    @Transactional
    public InvitationDto create(NewInvitationDto newInvitationDto) {
        if (newInvitationDto == null) {
            throw new IllegalArgumentException("NewInvitationDto must not be null");
        }
        SecurityUtils.requireRole("EMPLOYER");
        long employerIdFromToken = SecurityUtils.getCurrentUserId();

        Long jobId = newInvitationDto.getJobId();
        Long workerId = newInvitationDto.getWorkerId();

        if (jobId == null || workerId == null) {
            throw new IllegalArgumentException("NewInvitationDto must be full");
        }
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
        if (job.getEmployerId() == null || job.getEmployerId() != employerIdFromToken) {
            throw new AccessDeniedException("Forbidden");
        }

        List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId);
        for (Invitation invitation : invitationsForJob) {
            if (workerId.equals(invitation.getWorkerId()) && invitation.getStatus() == Invitation.Status.PENDING) {
                throw new IllegalArgumentException("Pending invitation already exists for job " + jobId +
                        " and worker " + workerId);
            }
        }
        Invitation invitation = new Invitation();
        invitation.setJobId(jobId);
        invitation.setWorkerId(workerId);
        invitation.setEmployerId(employerIdFromToken);
        invitation.setMessage(newInvitationDto.getMessage());

        Invitation saved = invitationRepository.save(invitation);
        return toDto(saved);
    }

    @Override
    @Transactional
    public InvitationDto changeStatus(long invitationId, Invitation.Status newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus must not be null");
        }

        if (newStatus == Invitation.Status.PENDING) {
            throw new IllegalArgumentException("Status PENDING cannot be set manually");
        }

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation with id " + invitationId + " not found"));

        if (invitation.getStatus() != Invitation.Status.PENDING) {
            throw new IllegalArgumentException("Only PENDING invitations can change status, status is " + invitation.getStatus());
        }

        long currentUserId = SecurityUtils.getCurrentUserId();

        if (newStatus == Invitation.Status.ACCEPTED || newStatus == Invitation.Status.REJECTED) {
            SecurityUtils.requireRole("WORKER");
            Long workerId = invitation.getWorkerId();
            if (workerId == null || workerId != currentUserId) {
                throw new AccessDeniedException("Forbidden");
            }
        } else if (newStatus == Invitation.Status.CANCELED) {
            SecurityUtils.requireRole("EMPLOYER");
            Long employerId = invitation.getEmployerId();
            if (employerId == null || employerId != currentUserId) {
                throw new AccessDeniedException("Forbidden");
            }
        } else {
            throw new IllegalArgumentException("Unsupported invitation status: " + newStatus);
        }

        if (newStatus == Invitation.Status.ACCEPTED) {
            Long jobId = invitation.getJobId();
            if (jobId == null) {
                throw new IllegalArgumentException("Invitation has no jobId");
            }
            Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
            if (job.getStatus() != JobStatus.OPEN) {
                throw new IllegalArgumentException("Job with id  " + jobId + " not open");
            }

            invitation.setStatus(Invitation.Status.ACCEPTED);
            Invitation saved = invitationRepository.save(invitation);

            job.setStatus(JobStatus.IN_PROGRESS);
            jobRepository.save(job);

            List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId);
            List<Invitation> toCancel = invitationsForJob.stream()
                    .filter(inv -> inv.getStatus() == Invitation.Status.PENDING)
                    .filter(inv -> inv.getId() == null || !inv.getId().equals(saved.getId()))
                    .peek(inv -> inv.setStatus(Invitation.Status.CANCELED))
                    .collect(Collectors.toList());
            if (!toCancel.isEmpty()) {
                invitationRepository.saveAll(toCancel);
            }

            try (Stream<Offer> stream = offerRepository.findByJobId(jobId)) {
                List<Offer> toReject = stream
                        .filter(o -> o.getStatus() == Offer.Status.PENDING)
                        .peek(o -> o.setStatus(Offer.Status.REJECTED))
                        .collect(Collectors.toList());

                if (!toReject.isEmpty()) {
                    offerRepository.saveAll(toReject);
                }
            }

            return toDto(saved);
        }

        invitation.setStatus(newStatus);
        Invitation updated = invitationRepository.save(invitation);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void closePendingInvitationsForJob(long jobId) {
        List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId);
        boolean updated = false;

        for (Invitation invitation : invitationsForJob) {
            if (invitation.getStatus() == Invitation.Status.PENDING) {
                invitation.setStatus(Invitation.Status.CANCELED);
                updated = true;
            }
        }

        if (updated) {
            invitationRepository.saveAll(invitationsForJob);
        }
    }

    private InvitationDto toDto(Invitation invitation) {
        if (invitation == null) {
            return null;
        }
        InvitationDto dto = new InvitationDto();
        dto.setId(invitation.getId());
        dto.setJobId(invitation.getJobId());
        dto.setWorkerId(invitation.getWorkerId());
        dto.setEmployerId(invitation.getEmployerId());
        dto.setStatus(invitation.getStatus());
        dto.setMessage(invitation.getMessage());
        dto.setCreatedAt(invitation.getCreatedAt());
        return dto;
    }
}





//package schabaschka.invitation.service;
//
//
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import schabaschka.invitation.dao.InvitationRepository;
//import schabaschka.invitation.dto.InvitationDto;
//import schabaschka.invitation.dto.NewInvitationDto;
//import schabaschka.invitation.model.Invitation;
//import schabaschka.job.dao.JobRepository;
//import schabaschka.job.model.Job;
//import schabaschka.security.SecurityUtils;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional(readOnly = true)
//public class InvitationServiceImpl implements InvitationService {
//    private final InvitationRepository invitationRepository;
//    private final JobRepository jobRepository;
//
//    public InvitationServiceImpl(InvitationRepository invitationRepository, JobRepository jobRepository) {
//        this.invitationRepository = invitationRepository;
//        this.jobRepository = jobRepository;
//    }
//
//
//    @Override
//    public List<InvitationDto> findByWorkerId(long workerId) {
//        SecurityUtils.requireRole("WORKER");
//        long currentUserId = SecurityUtils.getCurrentUserId();
//        if(currentUserId != workerId){
//            throw new AccessDeniedException("Forbidden");
//        }
//        List<Invitation> invitations = invitationRepository.findByWorkerId(workerId);
//        return invitations.stream().map(this::toDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<InvitationDto> findByEmployerId(long employerId) {
//        SecurityUtils.requireRole("EMPLOYER");
//        long currentUserId = SecurityUtils.getCurrentUserId();
//        if(currentUserId != employerId){
//            throw new AccessDeniedException("Forbidden");
//        }
//        List<Invitation> invitations = invitationRepository.findByEmployerId(employerId);
//        return invitations.stream().map(this::toDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public Optional<InvitationDto> findById(long id) {
//        Optional<Invitation> invitationOpt = invitationRepository.findById(id);
//        if(invitationOpt.isEmpty()){
//            return Optional.empty();
//        }
//        Invitation  invitation = invitationOpt.get();
//        long currentUserId = SecurityUtils.getCurrentUserId();
//        Long workerId = invitation.getWorkerId();
//        Long employerId = invitation.getEmployerId();
//
//        if(workerId != null && workerId == currentUserId){
//            SecurityUtils.requireRole("WORKER");
//            return Optional.of(toDto(invitation));
//        }
//        if(employerId != null && employerId == currentUserId){
//            SecurityUtils.requireRole("EMPLOYER");
//            return Optional.of(toDto(invitation));
//        }
//        throw new AccessDeniedException("Forbidden");
//
//
//
//    }
//
//
//    @Override
//    @Transactional
//    public InvitationDto create(NewInvitationDto newInvitationDto) {
//        if (newInvitationDto == null){
//            throw new IllegalArgumentException("NewInvitationDto must not be null");
//        }
//        SecurityUtils.requireRole("EMPLOYER");
//        long employerIdFromToken =  SecurityUtils.getCurrentUserId();
//
//        Long jobId = newInvitationDto.getJobId();
//        Long workerId = newInvitationDto.getWorkerId();
//
//        if(jobId == null || workerId == null ){
//            throw new IllegalArgumentException("NewInvitationDto must be full");
//        }
//        Job job = jobRepository.findById(jobId).orElseThrow(()-> new IllegalArgumentException("Job with id " + jobId + " not found"));
//        if(job.getEmployerId() == null || job.getEmployerId() != employerIdFromToken){
//            throw new AccessDeniedException("Forbidden");
//        }
//
//        List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId);
//        for (Invitation invitation : invitationsForJob){
//            if (workerId.equals(invitation.getWorkerId()) && invitation.getStatus() == Invitation.Status.PENDING){
//                throw new IllegalArgumentException("Pending invitation already exists for job " + jobId +
//                        " and worker " + workerId);
//            }
//        }
//        Invitation invitation = new Invitation();
//        invitation.setJobId(jobId);
//        invitation.setWorkerId(workerId);
//        invitation.setEmployerId(employerIdFromToken);
//        invitation.setMessage(newInvitationDto.getMessage());
//
//        Invitation saved = invitationRepository.save(invitation);
//        return toDto(saved);
//    }
//
//    @Override
//    @Transactional
//    public InvitationDto changeStatus(long invitationId, Invitation.Status newStatus) {
//        if (newStatus == null) {
//            throw new IllegalArgumentException("newStatus must not be null");
//        }
//
//        if (newStatus == Invitation.Status.PENDING) {
//            throw new IllegalArgumentException("Status PENDING cannot be set manually");
//        }
//
//        Invitation invitation = invitationRepository.findById(invitationId)
//                .orElseThrow(() -> new IllegalArgumentException("Invitation with id " + invitationId + " not found"));
//
//        if (invitation.getStatus() != Invitation.Status.PENDING) {
//            throw new IllegalArgumentException("Only PENDING invitations can change status, status is " + invitation.getStatus());
//        }
//
//        long currentUserId = SecurityUtils.getCurrentUserId();
//
//        if (newStatus == Invitation.Status.ACCEPTED || newStatus == Invitation.Status.REJECTED) {
//
//            SecurityUtils.requireRole("WORKER");
//            Long workerId = invitation.getWorkerId();
//            if (workerId == null || workerId != currentUserId) {
//                throw new AccessDeniedException("Forbidden");
//            }
//        } else if (newStatus == Invitation.Status.CANCELED) {
//
//            SecurityUtils.requireRole("EMPLOYER");
//            Long employerId = invitation.getEmployerId();
//            if (employerId == null || employerId != currentUserId) {
//                throw new AccessDeniedException("Forbidden");
//            }
//        } else {
//            throw new IllegalArgumentException("Unsupported invitation status: " + newStatus);
//        }
//
//        invitation.setStatus(newStatus);
//        Invitation updated = invitationRepository.save(invitation);
//        return toDto(updated);
//    }
//
//    @Override
//    @Transactional
//    public void closePendingInvitationsForJob(long jobId) {
//        List<Invitation> invitationsForJob = invitationRepository.findByJobId(jobId);
//        boolean updated = false;
//
//        for (Invitation invitation : invitationsForJob) {
//            if (invitation.getStatus() == Invitation.Status.PENDING) {
//                invitation.setStatus(Invitation.Status.CANCELED);
//                updated = true;
//            }
//        }
//
//        if (updated) {
//            invitationRepository.saveAll(invitationsForJob);
//        }
//    }
//
//
//
//    private InvitationDto toDto(Invitation invitation) {
//        if (invitation == null) {
//            return null;
//        }
//        InvitationDto dto = new InvitationDto();
//        dto.setId(invitation.getId());
//        dto.setJobId(invitation.getJobId());
//        dto.setWorkerId(invitation.getWorkerId());
//        dto.setEmployerId(invitation.getEmployerId());
//        dto.setStatus(invitation.getStatus());
//        dto.setMessage(invitation.getMessage());
//        dto.setCreatedAt(invitation.getCreatedAt());
//        return dto;
//    }
//}
