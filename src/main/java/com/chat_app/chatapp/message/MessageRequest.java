package com.chat_app.chatapp.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageRequest {

    private Long groupId;

    private Long senderId;

    private String content;

    private String messageType;
}
