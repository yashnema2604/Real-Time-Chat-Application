package com.chatApplication.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    private String content;

    private String recipient;

    private String color;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    public enum MessageType{
        CHAT,PRIVATE_MESSAGE,JOIN,LEAVE,TYPING
    }

}
