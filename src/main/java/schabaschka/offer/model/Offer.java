package schabaschka.offer.model;


import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "worker_id" , nullable = false)
    private Long workerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private Status status;

    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (status == null){
            status = Status.PENDING;
        }
        if(createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Offer() {
    }

    public Offer(Long id, Long jobId, Long workerId, Status status, String message, OffsetDateTime createdAt) {
        this.id = id;
        this.jobId = jobId;
        this.workerId = workerId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }
}
