package com.redgradient.bankapi.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Firstname cannot be empty")
    private String firstName;
    @NotBlank(message = "Lastname cannot be empty")
    private String lastName;
    private String middleName;

    @Size(min = 5, max = 20, message = "Username length must be from 5 to 20 characters")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 5, max = 255, message = "Email address must be from 5 to 255 characters")
    @Email(message = "Email address must follow the format user@example.com")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be from 10 to 15 characters")
    private String phoneNumber;

    @Size(min = 8, max = 255, message = "Password length must be from 8 to 255 characters")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
