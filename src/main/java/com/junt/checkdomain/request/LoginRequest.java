package com.junt.checkdomain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequest {

    @NotBlank(message = "Please enter username.")
    private String username;

    @NotBlank(message = "Please enter password.")
    private String password;
}
