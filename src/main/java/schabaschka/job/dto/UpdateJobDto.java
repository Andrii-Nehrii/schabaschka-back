package schabaschka.job.dto;

import schabaschka.job.JobCategory;

import java.math.BigDecimal;

public class UpdateJobDto {
    String title;
    String description;
    private String city;
    private JobCategory category;
    private BigDecimal price;

    public UpdateJobDto() {
    }

    public UpdateJobDto(String title, String description, String city, JobCategory category, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.category = category;
        this.price = price;
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
}
