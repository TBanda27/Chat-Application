package com.chat_app.chatapp.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestUser {

    private String firstName;

    private String lastName;

    private String email;
}
