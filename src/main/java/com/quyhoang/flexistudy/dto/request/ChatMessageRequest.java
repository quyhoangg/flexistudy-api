package com.quyhoang.flexistudy.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequest {
    String userId;
    String companyId;
    String senderId;
    String receiverId;
    String content;
}
