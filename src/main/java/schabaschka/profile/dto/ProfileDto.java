package schabaschka.profile.dto;

public class ProfileDto {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ProfileDto(Long id, Long userId, String name, String phone, String city) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.city = city;
    }

    public ProfileDto() {
    }
}
