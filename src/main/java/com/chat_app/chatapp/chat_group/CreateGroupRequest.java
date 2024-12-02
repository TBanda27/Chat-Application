package com.chat_app.chatapp.chat_group;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CreateGroupRequest {

    private String groupName;

    private Long creatorId;

}
