package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String id;
    String conversationId;
    String senderId;
    String receiverId;
    String content;
    LocalDateTime sentAt;
    boolean read;
}
