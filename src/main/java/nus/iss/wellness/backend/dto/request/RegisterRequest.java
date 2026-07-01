package nus.iss.wellness.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//author: Junior

public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public RegisterRequest() {
        super();
    }

    public RegisterRequest(@NotBlank(message = "Username is required") String username,
                           @Email(message = "Invalid email") String email,
                           @NotBlank(message = "Password is required") String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
