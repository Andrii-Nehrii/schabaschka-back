package schabaschka.auth.dto;

public class AuthLoginRequestDto {
    private String email;
    private String password;

    public AuthLoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthLoginRequestDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
