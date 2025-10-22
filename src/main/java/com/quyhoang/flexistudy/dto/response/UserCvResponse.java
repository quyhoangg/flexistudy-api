package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCvResponse {
    String id;
    String fileName;
    String fileUrl;
    String fileType;
    long fileSize;
    LocalDateTime uploadedAt;
    boolean isPrimary;
}
