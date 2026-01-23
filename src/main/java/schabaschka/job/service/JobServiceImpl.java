package schabaschka.job.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schabaschka.invitation.service.InvitationService;
import schabaschka.job.JobCategory;
import schabaschka.job.JobStatus;
import schabaschka.job.dao.JobRepository;
import schabaschka.job.dto.JobDto;
import schabaschka.job.dto.NewJobDto;
import schabaschka.job.dto.UpdateJobDto;
import schabaschka.job.model.Job;
import schabaschka.offer.service.OfferService;
import schabaschka.profile.dto.ProfileDto;
import schabaschka.profile.service.ProfileService;
import schabaschka.security.SecurityUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Transactional(readOnly = true)
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final ProfileService profileService;
    private final OfferService offerService;
    private final InvitationService invitationService;

    public JobServiceImpl(JobRepository jobRepository, ProfileService profileService, OfferService offerService, InvitationService invitationService) {
        this.jobRepository = jobRepository;
        this.profileService = profileService;
        this.offerService = offerService;
        this.invitationService = invitationService;
    }


    @Override
    public List<JobDto> find(String city, JobCategory category, String q, int page, int size) {
        String normCity = normalize(city);
        String normQ = normalize(q);
        int pageIndex = Math.max(0, page);
        int pageSize = Math.max(1, size);
        long toSkip = (long) pageIndex * pageSize;

           try (Stream<Job> stream = jobRepository.findAllBy()){
            Comparator<Job> byCreatedDesc = Comparator.comparing(Job::getCreatedAt).reversed();
            return stream.filter(j-> normCity == null ||(j.getCity() != null && (j.getCity().equalsIgnoreCase(normCity))))
                    .filter(j -> category ==null || (j.getCategory() == category))
                    .filter(j -> {
                        if(normQ == null) return true;
                        String title = j.getTitle();
                        String desc  = j.getDescription();
                        String qLower = normQ.toLowerCase(Locale.ROOT);
                        return (title != null && title.toLowerCase(Locale.ROOT).contains(qLower)) || (desc != null && desc.toLowerCase(Locale.ROOT).contains(qLower));
                    })
                    .sorted(byCreatedDesc).skip(toSkip).limit(pageSize).map(this :: toDto).collect(Collectors.toList());




        }


    }

    @Override
    public Page<JobDto> findPage(String city, JobCategory category, String q, int page, int size) {
        int pageIndex = Math.max(0, page);
        int pageSize  = Math.max(1, size);

        long total = count(city, category, q);
        List<JobDto> items = find(city, category, q, pageIndex, pageSize);

        return new PageImpl<>(items, PageRequest.of(pageIndex, pageSize), total);
    }

    @Override
    public long count(String city, JobCategory category, String q) {

        final String normCity = normalize(city);
        final String normQ    = normalize(q);

        try (Stream<Job> stream = jobRepository.findAllBy()) {
            return stream
                    .filter(j -> normCity == null
                            || (j.getCity() != null && j.getCity().equalsIgnoreCase(normCity)))
                    .filter(j -> category == null || j.getCategory() == category)
                    .filter(j -> {
                        if (normQ == null) return true;
                        String title = j.getTitle();
                        String desc  = j.getDescription();
                        String qLower = normQ.toLowerCase(Locale.ROOT);
                        return (title != null && title.toLowerCase(Locale.ROOT).contains(qLower))
                                || (desc  != null && desc.toLowerCase(Locale.ROOT).contains(qLower));
                    })
                    .count();
        }
    }

    @Override
    public Optional<JobDto> findById(long id) {
        return jobRepository.findById(id).map(this::toDto);

    }

    @Override
    @Transactional
    public JobDto create(long employerId, NewJobDto newJobDto) {
        SecurityUtils.requireRole("EMPLOYER");
        long currentUserId = SecurityUtils.getCurrentUserId();

        if(employerId != currentUserId){
            throw new AccessDeniedException("Forbidden");
        }


        if(newJobDto == null){
            throw new IllegalArgumentException("newJobDto is null");
        }
        Job job = new Job();
        job.setEmployerId(currentUserId);
        job.setTitle(newJobDto.getTitle());
        job.setDescription(newJobDto.getDescription());
        job.setCity(newJobDto.getCity());
        job.setCategory(newJobDto.getCategory());
        job.setPrice(newJobDto.getPrice());
        Job saved = jobRepository.save(job);
        return toDto(saved);
    }

    @Override
    @Transactional
    public JobDto update(long employerId, long jobId, UpdateJobDto updateJobDto) {
        SecurityUtils.requireRole("EMPLOYER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        if(employerId != currentUserId){
            throw new AccessDeniedException("Forbidden");
        }
        if (updateJobDto == null) {
            throw new IllegalArgumentException("updateJobDto must not be null");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
        Long employerIdFromDb = job.getEmployerId();

        if (employerIdFromDb == null || employerIdFromDb != currentUserId) {
            throw new IllegalArgumentException( "Job with id " + jobId + " does not belong to employer " + employerId + "or Forbidden");
        }
        job.setTitle(updateJobDto.getTitle());
        job.setDescription(updateJobDto.getDescription());
        job.setCity(updateJobDto.getCity());
        job.setCategory(updateJobDto.getCategory());
        job.setPrice(updateJobDto.getPrice());
        Job saved = jobRepository.save(job);

        return toDto(saved);
    }

    @Override
    @Transactional
    public JobDto changeStatus(long employerId, long jobId, JobStatus newStatus) {
        SecurityUtils.requireRole("EMPLOYER");
        long currentUserId = SecurityUtils.getCurrentUserId();
        if(employerId != currentUserId){
            throw new AccessDeniedException("Forbidden");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus must not be null");
        }
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
        Long employerIdFromDb = job.getEmployerId();
        if (employerIdFromDb == null || employerIdFromDb != currentUserId) {
            throw new IllegalArgumentException( "Job with id " + jobId + " does not belong to employer " + employerId);
        }
        job.setStatus(newStatus);
        Job saved =  jobRepository.save(job);

        if(newStatus == JobStatus.DONE){
            offerService.closePendingOffersForJob(jobId);
            invitationService.closePendingInvitationsForJob(jobId);
        }

        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(long employerId, long jobId) {
        SecurityUtils.requireRole("EMPLOYER");
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if(employerId != currentUserId){
            throw new AccessDeniedException("Forbidden");
        }
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job with id " + jobId + " not found"));
        Long employerIdFromDb = job.getEmployerId();
        if (employerIdFromDb == null || employerIdFromDb != currentUserId) {
            throw new IllegalArgumentException("Job with id " + jobId + " does not belong to employer " + employerId);
        }
        jobRepository.delete(job);

    }


    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private JobDto toDto(Job j) {
        JobDto dto = new JobDto();
        dto.setId(j.getId());
        dto.setEmployerId(j.getEmployerId());
        dto.setTitle(j.getTitle());
        dto.setDescription(j.getDescription());
        dto.setCategory(j.getCategory());
        dto.setCity(j.getCity());
        dto.setPrice(j.getPrice());
        dto.setStatus(j.getStatus());
        dto.setCreatedAt(j.getCreatedAt());
        Long employerId = j.getEmployerId();
        if (employerId != null) {

            Optional<ProfileDto> profileOpt = profileService.findByUserId(employerId);
            if (profileOpt.isPresent()) {
                ProfileDto profile = profileOpt.get();
                dto.setEmployerName(profile.getName());
                dto.setPhone(profile.getPhone());
            }
        }


        return dto;
    }

}
