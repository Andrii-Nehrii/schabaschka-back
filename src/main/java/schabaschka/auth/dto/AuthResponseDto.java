package schabaschka.auth.dto;

public class AuthResponseDto {
    private long userId;
    private String email;
    private String role;
    private String name;
    private String surname;
    private String phone;
    private String[] categories;
    private String city;
    private String token;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AuthResponseDto() {
    }

    public AuthResponseDto(long userId, String email, String role, String name, String surname, String phone, String[] categories, String city, String token) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.categories = categories;
        this.city = city;
        this.token = token;
    }
}
