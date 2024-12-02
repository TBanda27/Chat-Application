package com.chat_app.chatapp.user;

import com.chat_app.chatapp.chat_group.ChatGroup;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;


@Service
@AllArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public User addUser(RequestUser requestUser) {
        User user = User.builder()
                .firstName(requestUser.getFirstName())
                .lastName(requestUser.getLastName())
                .email(requestUser.getEmail())
                .build();

        logger.info("Adding user: {}", user.getEmail());
        userRepository.save(user);  // Consider using save() instead of saveAndFlush()
        return user;
    }

    public List<ChatGroup> getGroupsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found."));
        logger.info("Fetching chat groups for user ID: {}", userId);
        return new ArrayList<>(user.getChatGroups());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));
    }
}

