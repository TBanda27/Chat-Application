package com.chat_app.chatapp.chat_group;

import com.chat_app.chatapp.message.Message;
import com.chat_app.chatapp.message.MessageRepository;
import com.chat_app.chatapp.message.MessageType;
import com.chat_app.chatapp.user.User;
import com.chat_app.chatapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group-chat")
@Slf4j
public class ChatGroupController {

    private final ChatGroupService chatGroupService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<ChatGroup> createChatGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        log.info("Request to create a group made by with name:: {} and created by :: {}", createGroupRequest.getCreatorId(), createGroupRequest.getGroupName());
        ChatGroup chatGroup = chatGroupService.createGroupChat(createGroupRequest);
        chatGroupService.addUserToGroup(chatGroup.getId(), createGroupRequest.getCreatorId());

        User user = userRepository.findById(createGroupRequest.getCreatorId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        // Create a join message for the creator
        Message joinMessage = new Message();
        joinMessage.setContent(user.getFirstName() + " " + user.getLastName() + " has created the group and joined.");
        joinMessage.setSender(user);
        joinMessage.setChatGroup(chatGroup);
        joinMessage.setMessageType(MessageType.JOIN);
        joinMessage.setCreatedAt(LocalDateTime.now());

        // Send the join message to the group topic
        messageRepository.save(joinMessage);
        messagingTemplate.convertAndSend("/topic/group/" + chatGroup.getId(), joinMessage);

        return ResponseEntity.ok(chatGroup);
    }

//    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/group/{groupId}")
//    public Message addUser(
//            @Payload Message message,
//            SimpMessageHeaderAccessor headerAccessor
//    ) {
//        log.info("Request to add {} to group-chat {}", message.getSender(), message.getChatGroup().getGroupName());
//        Long groupId = message.getChatGroup().getId(); // Get the groupId from the chat group
//        String username = message.getSender().getFirstName() + " " + message.getSender().getLastName();
//
//        if (headerAccessor != null) {
//            headerAccessor.getSessionAttributes().put("username", username);
//        }
//
//        // Create a message indicating the user has joined
//        Message messageToSend = new Message();
//        messageToSend.setChatGroup(message.getChatGroup());
//        messageToSend.setContent(username + " has joined the group.");
//        messageToSend.setMessageType(MessageType.JOIN);
//        messageToSend.setCreatedAt(LocalDateTime.now());
//        messageToSend.setSender(message.getSender());
//
//        // Save the join message to the repository
//        messageRepository.save(messageToSend);
//
//        log.info("{} User has successfully registered to a topic", message.getSender());
//        return messageToSend; // Return the message to send to the topic
//    }
}



