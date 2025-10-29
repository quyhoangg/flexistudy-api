package com.quyhoang.flexistudy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    Conversation conversation;

    @Column(nullable = false)
    String senderId;

    @Column(nullable = false)
    String receiverId;

    @Column(nullable = false, length = 2000)
    String content;

    @CreationTimestamp
    LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    boolean read = false;
}
