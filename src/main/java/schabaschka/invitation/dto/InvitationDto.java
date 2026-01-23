package schabaschka.invitation.dto;

import schabaschka.invitation.model.Invitation;

import java.time.OffsetDateTime;

public class InvitationDto {
private Long id;
private Long jobId;
private Long workerId;
private Long employerId;
private Invitation.Status status;
private OffsetDateTime createdAt;
private String message;


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

    public Invitation.Status getStatus() {
        return status;
    }

    public void setStatus(Invitation.Status status) {
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

    public InvitationDto() {
    }

    public InvitationDto(Long id, Long jobId, Long workerId, Long employerId, Invitation.Status status, OffsetDateTime createdAt, String message) {
        this.id = id;
        this.jobId = jobId;
        this.workerId = workerId;
        this.employerId = employerId;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }
}
