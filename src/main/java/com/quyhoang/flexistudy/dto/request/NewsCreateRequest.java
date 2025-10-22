package com.quyhoang.flexistudy.dto.request;

import com.quyhoang.flexistudy.enums.ContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsCreateRequest {
    @NotBlank
    String title;
    String body;
    String summary;
    String category;
    String imageUrl;
    ContentStatus status;

    LocalDateTime publishAt;
}
