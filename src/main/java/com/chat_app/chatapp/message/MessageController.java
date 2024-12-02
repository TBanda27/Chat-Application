package com.chat_app.chatapp.message;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
//@MessageMapping("/api/v1/messages")
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat/send")
    @SendTo("/topic/group")
    public Message sendMessage(@Payload MessageRequest messageRequest, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Request to send a message :: {}", messageRequest);
        return messageService.sendMessage(messageRequest);
    }

    // New method to join a chat group
    @MessageMapping("/chat/join")
    @SendTo("/topic/group/{groupId}")
    public void joinGroup(@Payload JoinRequest joinRequest, SimpMessageHeaderAccessor headerAccessor) {
        // Logic to add user to the chat group
        messageService.joinGroup(joinRequest);
    }
}
