package com.LuckyHub.Backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordModel {

    @NotBlank(message = "Current password cannot be empty")
    private String currentPassword;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    private String newPassword;

    @NotBlank(message = "Confirm new password cannot be empty")
    @Size(min = 8, message = "Confirm password must be at least 8 characters long")
    private String confirmNewPassword;
}
