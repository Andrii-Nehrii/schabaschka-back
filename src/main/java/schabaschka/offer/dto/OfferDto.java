package schabaschka.offer.dto;

import schabaschka.offer.model.Offer;

import java.time.OffsetDateTime;

public class OfferDto {
    private Long id;
    private Long jobId;
    private Long workerId;
    private Offer.Status status;
    private String message;
    private OffsetDateTime createdAt;


    public OfferDto() {
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

    public Offer.Status getStatus() {
        return status;
    }

    public void setStatus(Offer.Status status) {
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

    public OfferDto(Long id, Long jobId, Long workerId, Offer.Status status, String message, OffsetDateTime createdAt) {
        this.id = id;
        this.jobId = jobId;
        this.workerId = workerId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }
}
