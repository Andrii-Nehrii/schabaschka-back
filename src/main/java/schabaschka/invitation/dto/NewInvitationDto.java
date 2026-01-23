package schabaschka.invitation.dto;

public class NewInvitationDto {
    private Long jobId;
    private Long workerId;
    private Long employerId;
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NewInvitationDto(Long jobId, Long workerId, Long employerId, String message) {
        this.jobId = jobId;
        this.workerId = workerId;
        this.employerId = employerId;
        this.message = message;
    }

    public NewInvitationDto() {
    }
}
