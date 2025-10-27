package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    String id;

    String companyId;
    String companyName;

    String userId;
    String userFullName;
    String userEmail;

    Integer star;
    String comment;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
