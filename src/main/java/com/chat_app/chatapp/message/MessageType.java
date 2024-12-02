package com.chat_app.chatapp.message;

public enum MessageType {
    CHAT,        // Regular chat_group message
    JOIN,        // User joins the chat_group
    LEAVE,       // User leaves the chat_group
    CONNECT,     // User connects to the chat_group (initial join)
    DISCONNECT,  // User disconnects from the chat_group
    TYPING,      // User is typing a message
    FILE_SHARE,  // File sharing
    IMAGE_SHARE, // Image sharing
    VIDEO_SHARE, // Video sharing
    GROUP_CREATE,// Group creation
    GROUP_UPDATE,// Group update (e.g., name change)
    GROUP_DELETE,// Group deletion
    MEMBER_ADD,  // Add member to group
    MEMBER_REMOVE // Remove member from group
}

