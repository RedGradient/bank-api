package com.redgradient.bankapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Size(min = 5, max = 255, message = "Email address must be from 5 to 255 characters")
    @Email(message = "Email address must follow the format user@example.com")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be from 10 to 15 characters")
    private String phoneNumber;
}
