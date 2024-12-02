package com.chat_app.chatapp.user;

import com.chat_app.chatapp.chat_group.ChatGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<User> registerUser(@RequestBody RequestUser requestUser){
        log.info("Request to add user:: {}", requestUser);
        return ResponseEntity.ok(userService.addUser(requestUser));

    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginRequest loginRequest){
        log.info("Request to login user:: {} ", loginRequest.getEmail());
        User user = userService.findUserByEmail(loginRequest.getEmail());
        log.info("User logged in:: {}", user);
        return ResponseEntity.ok(userService.findUserByEmail(loginRequest.getEmail()));
    }

    @GetMapping("/groups/{userId}")
    public ResponseEntity<List<ChatGroup>> getGroupsForUser(@PathVariable Long userId) {
        log.info("Request to get all groups for user with id:: {}", userId);
        List<ChatGroup> groups = userService.getGroupsForUser(userId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping()
    public ResponseEntity<List<User>>  getAllUsers(){
        log.info("Request to get a list of all users:: ");
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
