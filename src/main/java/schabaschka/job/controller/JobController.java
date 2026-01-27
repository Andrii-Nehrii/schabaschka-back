package schabaschka.job.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schabaschka.job.JobCategory;
import schabaschka.job.JobStatus;
import schabaschka.job.dto.JobDto;
import schabaschka.job.dto.NewJobDto;
import schabaschka.job.dto.UpdateJobDto;
import schabaschka.job.service.JobService;
import schabaschka.security.JwtTokenService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import schabaschka.security.SecurityUtils;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    @GetMapping
    public Page<JobDto> list(
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String q,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size
    ){
        JobCategory cat = parseCategory(category);
        return jobService.findPage(city, cat, q, page, size);
    }

    @GetMapping("/{id}")
        public ResponseEntity<JobDto> getById(@PathVariable("id") long id){
        Optional<JobDto> jobOpt = jobService.findById(id);
        return jobOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return Arrays.stream(JobCategory.values())
                .map(Enum::name)
                .toList();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobDto create(@RequestBody NewJobDto newJobDto){
       SecurityUtils.requireRole("EMPLOYER");
        long employerId = SecurityUtils.getCurrentUserId();
        return jobService.create(employerId, newJobDto);
    }


    @PutMapping("/{id}")
    public JobDto update(@PathVariable("id") long jobId, @RequestBody UpdateJobDto updateJobDto){
        SecurityUtils.requireRole("EMPLOYER");
        long employerId = SecurityUtils.getCurrentUserId();
       return jobService.update(employerId,jobId , updateJobDto);
    }

    @PatchMapping("/{id}/status")
    public JobDto changeStatus(@PathVariable("id") long jobId, @RequestParam("status") String status){
        JobStatus newStatus = parseStatus(status);
        if (newStatus == null) {
            throw new IllegalArgumentException("Unknown job status: " + status);
        }
        SecurityUtils.requireRole("EMPLOYER");
        long employerId = SecurityUtils.getCurrentUserId();
        return jobService.changeStatus(employerId, jobId, newStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long jobId) {
        SecurityUtils.requireRole("EMPLOYER");
        long employerId = SecurityUtils.getCurrentUserId();

        jobService.delete(employerId, jobId);
    }
    @GetMapping("/my")
    public Page<JobDto> myJobs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        SecurityUtils.requireRole("EMPLOYER");
        long employerId = SecurityUtils.getCurrentUserId();
        JobStatus st = parseStatus(status);
        return jobService.findMyPage(employerId, st, q, page, size);
    }



    private JobCategory parseCategory(String s) {
            if (s == null) return null;

            String t = s.trim();
            if (t.isEmpty()) return null;

            String up = t.toUpperCase(Locale.ROOT);
            for (JobCategory c : JobCategory.values()) {
                if (c.name().equals(up)) return c;
            }
            return null;
    }
    private JobStatus parseStatus(String s) {
        if (s == null) {
            return null;
        }

        String t = s.trim();
        if (t.isEmpty()) {
            return null;
        }

        String up = t.toUpperCase(Locale.ROOT);
        for (JobStatus status : JobStatus.values()) {
            if (status.name().equals(up)) {
                return status;
            }
        }
        return null;
    }



}
