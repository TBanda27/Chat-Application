package com.chat_app.chatapp.user;


import lombok.Data;

@Data
public class LoginRequest {

    private String email;

    private String password;

}