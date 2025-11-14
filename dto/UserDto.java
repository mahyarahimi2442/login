package ir.ac.loging.dto;

import jakarta.validation.constraints.NotBlank;

public class UserDto {
    private  String username;
    private  String fullName;
    private  String email;
    private  String password;


    public UserDto() {}

    public UserDto(String username, String fullName, String email, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    @NotBlank(message = "user.username.null")
    public String getUsername() {
        return username;
    }

    @NotBlank(message = "user.password.null")
    public String getPassword() {
        return password;
    }

    @NotBlank(message = "user.fullName.null")
    public String getFullName() {
        return fullName;
    }


    @NotBlank(message = "user.email.null")
    public String getEmail() {
        return email;
    }
}

