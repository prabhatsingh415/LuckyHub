package com.LuckyHub.Backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeNameRequest {

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "First name can only contain letters"
    )
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    @Pattern(
            regexp = "^[A-Za-z]+$",
            message = "Last name can only contain letters"
    )
    private String lastName;
}
