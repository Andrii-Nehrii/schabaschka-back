package schabaschka.job.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import schabaschka.job.JobCategory;
import schabaschka.job.model.Job;

import java.util.stream.Stream;


public interface JobRepository extends JpaRepository<Job, Long> {


    Stream<Job> findAllBy();


    Stream<Job> findByCityIgnoreCase(String city);
    Stream<Job> findByCategory(JobCategory category);


    Stream<Job> findByCityIgnoreCaseAndCategory(String city, JobCategory category);


    Stream<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String q1, String q2
    );


    Stream<Job> findByCityIgnoreCaseAndTitleContainingIgnoreCaseOrCityIgnoreCaseAndDescriptionContainingIgnoreCase(
            String city1, String q1,
            String city2, String q2
    );


    Stream<Job> findByCategoryAndTitleContainingIgnoreCaseOrCategoryAndDescriptionContainingIgnoreCase(
            JobCategory category1, String q1,
            JobCategory category2, String q2
    );


    Stream<Job> findByCityIgnoreCaseAndCategoryAndTitleContainingIgnoreCaseOrCityIgnoreCaseAndCategoryAndDescriptionContainingIgnoreCase(
            String city1, JobCategory category1, String q1,
            String city2, JobCategory category2, String q2
    );
}

