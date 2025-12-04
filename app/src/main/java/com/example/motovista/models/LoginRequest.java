package com.example.motovista.models;

public class LoginRequest {
    private String login; // can be email or username
    private String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
