package schabaschka.job.service;

import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.domain.Page;
import schabaschka.job.JobCategory;
import schabaschka.job.JobStatus;
import schabaschka.job.dto.JobDto;
import schabaschka.job.dto.NewJobDto;
import schabaschka.job.dto.UpdateJobDto;

import java.util.List;
import java.util.Optional;

public interface JobService {
    List<JobDto> find(String city, JobCategory category, String q , int page, int size);
    Page<JobDto> findPage(String city, JobCategory category, String q , int page, int size);
    long count(String city, JobCategory category, String q);
    Optional<JobDto> findById(long id);

    JobDto create(long employerId, NewJobDto newJobDto);

    JobDto update(long employerId, long jobId, UpdateJobDto updateJobDto);

    JobDto changeStatus(long employerId, long jobId, JobStatus newStatus);

    void delete(long employerId, long jobId);


}
