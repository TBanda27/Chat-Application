package com.chat_app.chatapp.chat_group;

import com.chat_app.chatapp.user.User;
import com.chat_app.chatapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGroupService {

    private final ChatGroupRepository chatGroupRepository;

    private final UserRepository userRepository;


    public ChatGroup createGroupChat(CreateGroupRequest request) {
        log.info("Creating chat group {}", request.getGroupName());
        Optional<User> creatorOpt = userRepository.findById(request.getCreatorId());
        if (creatorOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        ChatGroup chatGroup = new ChatGroup(request.getGroupName());
        return chatGroupRepository.save(chatGroup);
    }

    public void addUserToGroup(Long groupId, Long userId) {
        ChatGroup chatGroup = chatGroupRepository.findById(groupId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        Set<User> users = chatGroup.getParticipants();
        users.add(user);
        chatGroup.setParticipants(users);
        chatGroupRepository.save(chatGroup);
    }


}
