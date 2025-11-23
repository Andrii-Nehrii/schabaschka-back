package schabaschka.job.dto;

import schabaschka.job.JobCategory;
import schabaschka.job.JobStatus;

import java.math.BigDecimal;
import java.time.Instant;


public class JobDto {
    private Long id;
    private Long employerId;
    private JobStatus status;
    private String title;
    private String description;
    private String city;
    private JobCategory category;
    private BigDecimal price;
    private Instant createdAt;
    private String employerName;
    private String phone;

    public JobDto() {
    }

    public JobDto(Long id, Long employerId, JobStatus status, String title, String description, String city, JobCategory category, BigDecimal price, Instant createdAt, String employerName, String phone) {
        this.id = id;
        this.employerId = employerId;
        this.status = status;
        this.title = title;
        this.description = description;
        this.city = city;
        this.category = category;
        this.price = price;
        this.createdAt = createdAt;
        this.employerName = employerName;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDto jobDto = (JobDto) o;
        if(this.id == null || jobDto.id == null )return false;
        return this.id.equals(jobDto.id);

    }

    @Override
    public int hashCode() {
        return id !=null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "JobDto{" +
                "id=" + id +
                ", employerId=" + employerId +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", category=" + category +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", employerName='" + employerName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public Long getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Long employerId) {
        this.employerId = employerId;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public JobCategory getCategory() {
        return category;
    }

    public void setCategory(JobCategory category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
