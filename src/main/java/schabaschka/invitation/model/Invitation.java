package schabaschka.invitation.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "worker_id" , nullable = false)
    private Long workerId;

    @Column(name ="employer_id", nullable = false)
    private Long employerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private String message;


    @PrePersist
     public void prePersist(){
         if(status == null){
             status = Status.PENDING;
         }
         if(createdAt == null){
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

    public Long getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Long employerId) {
        this.employerId = employerId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Invitation(Long id, Long jobId, Long workerId, Long employerId, Status status, OffsetDateTime createdAt, String message) {
        this.id = id;
        this.jobId = jobId;
        this.workerId = workerId;
        this.employerId = employerId;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Invitation() {
    }
}
