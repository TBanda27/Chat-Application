package com.chat_app.chatapp.message;

import com.chat_app.chatapp.chat_group.ChatGroup;
import com.chat_app.chatapp.chat_group.ChatGroupRepository;
import com.chat_app.chatapp.user.User;
import com.chat_app.chatapp.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    private final ChatGroupRepository chatGroupRepository;

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Message sendMessage(MessageRequest messageRequest) {
        ChatGroup chatGroup = chatGroupRepository.findById(messageRequest.getGroupId()).orElseThrow(
                () -> new IllegalArgumentException("Group chat with id: "+ messageRequest.getGroupId() + " not found")
        );
        User sender = userRepository.findById(messageRequest.getSenderId()).orElseThrow(
                () -> new IllegalArgumentException("User with id: "+ messageRequest.getSenderId() + " not found")
        );

        Message message = Message.builder()
                .content(sender.getFirstName() +" has send"+ messageRequest.getContent())
                .sender(sender)
                .chatGroup(chatGroup)
                .createdAt(LocalDateTime.now())
                .messageType(MessageType.valueOf(messageRequest.getMessageType()))
                .build();

        // Update chatGroup messages
        chatGroup.getMessages().add(message);
        messagingTemplate.convertAndSend("/topic/group/" + message.getChatGroup().getId(), message);
        log.info("Message successfully sent by :: {} with content :: {}", sender.getFirstName() , messageRequest.getContent() + "to group " + message.getChatGroup().getGroupName());

        // Save message and update chatGroup
        return messageRepository.save(message);
    }

    @Transactional
    public void joinGroup(JoinRequest joinRequest) {
        log.info("Request to join a group chat with id :: {} with user id:: {}", joinRequest.getGroupId(), joinRequest.getUserId());
        ChatGroup chatGroup = chatGroupRepository.findById(joinRequest.getGroupId()).orElseThrow(
                () -> new IllegalArgumentException("Group chat with id: " + joinRequest.getGroupId() + " not found")
        );
        User user = userRepository.findById(joinRequest.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("User with id: " + joinRequest.getUserId() + " not found")
        );
        chatGroup.getParticipants().add(user);

        Message message = Message.builder()
                .sender(user)
                .chatGroup(chatGroup)
                .createdAt(LocalDateTime.now())
                .messageType(MessageType.JOIN)
                .content(user.getFirstName() + " has joined the group " + chatGroup.getGroupName())
                .build();

        // Update chatGroup messages
        chatGroup.getMessages().add(message);
        messageRepository.save(message);
        chatGroupRepository.save(chatGroup);

        messagingTemplate.convertAndSend("/topic/group/" + message.getChatGroup().getId(), message);
        log.info("Join Message successfully sent by :: {} with content :: {}", message.getSender().getFirstName(), message.getContent() + "to group " + message.getChatGroup().getGroupName() );

    }
}
