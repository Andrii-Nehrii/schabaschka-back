package schabaschka.job.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import schabaschka.job.JobCategory;
import schabaschka.job.JobStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employer_id")
    private Long employerId;


    private String title;
    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    private JobCategory category;

    private String city;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(updatable = false,insertable = false, columnDefinition = "timestamp with time zone default now()")
    private Instant createdAt;


@PrePersist
    private void prePersist(){
    if(this.status == null){
        this.status =JobStatus.OPEN;
    }

}

    public Job() {
    }

    public Job(Long id, Long employerId, String title, String description, JobCategory category, String city, BigDecimal price, JobStatus status, Instant createdAt) {
        this.id = id;
        this.employerId = employerId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.city = city;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Job)) return false;
        Job other = (Job) o;
        if (this.id == null || other.id == null) return false;
        return Objects.equals(this.id, other.id);
    }
    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

}
