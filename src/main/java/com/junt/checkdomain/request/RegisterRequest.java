package com.junt.checkdomain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullname;
}
