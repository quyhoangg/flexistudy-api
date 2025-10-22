package com.quyhoang.flexistudy.dto.response;

import com.quyhoang.flexistudy.enums.ContentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsResponse {
    String id;
    String title;
    String summary;
    String body;
    String imageUrl;
    ContentStatus status;
    String category;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime publishAt;
    String authorId;
    String authorName;
}
