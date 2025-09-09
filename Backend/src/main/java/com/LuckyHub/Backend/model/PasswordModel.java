package com.LuckyHub.Backend.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordModel {

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email cannot be empty!")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String oldPassword;

    @NotBlank(message = "Password cannot be empty")
    private String newPassword;
}