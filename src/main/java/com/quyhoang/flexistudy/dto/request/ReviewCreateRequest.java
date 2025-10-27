package com.quyhoang.flexistudy.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewCreateRequest {
    @NotBlank
    String companyId;

    @NotNull
    @Min(1) @Max(5)
    Integer star;

    @Size(max = 1000)
    String comment;
}
