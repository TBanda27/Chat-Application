package com.chat_app.chatapp.chat_group;


import com.chat_app.chatapp.message.Message;
import com.chat_app.chatapp.user.User;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ChatGroup {

    public ChatGroup(String name){
        this.groupName = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    @OneToMany(mappedBy = "chatGroup")
    private List<Message> messages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_chat_group",
            joinColumns = @JoinColumn(name = "chat_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
//    @JsonManagedReference
    @JsonIgnore
    private Set<User> participants = new HashSet<>();

    @Override
    public String toString() {
        return "ChatGroup{" +
                "id=" + id +
                ",groupName='" + groupName + '\'' +
                ",messages='" + messages + '\'' +
                ",participants='" + participants + '\'' +
                '}';
    }
}
