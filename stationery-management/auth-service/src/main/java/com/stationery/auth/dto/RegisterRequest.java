package com.stationery.auth.dto;

import com.stationery.auth.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


//Its job is to act as a container for the data that's been sent by the browser, after a user clicks on submit button on registration form. 
//It also checks that the fields are'nt empty and email is in a valid format before letting the code continue.

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    private String adminSecretCode; 
}