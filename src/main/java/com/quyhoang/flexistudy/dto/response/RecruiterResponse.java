package com.quyhoang.flexistudy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruiterResponse {
    String userId;
    String email;
    String fullName;
    String role;
    CompanyResponse company;
}
