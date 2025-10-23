package com.quyhoang.flexistudy.dto.response;

import com.quyhoang.flexistudy.enums.VerificationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyResponse {
    String id;
    String name;
    String description;
    String logoUrl;
    String website;
    int memberNumber;
    VerificationStatus verificationStatus;
    String verificationImageUrl;
    LocalDateTime verificationSubmittedAt;
    LocalDateTime verifiedAt;
    String verificationNote;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
