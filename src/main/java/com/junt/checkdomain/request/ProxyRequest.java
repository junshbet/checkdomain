package com.junt.checkdomain.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProxyRequest {
    private String id;
    private String name;
    private String proxy;
    private int port;
    private String username;
    private String password;
}
