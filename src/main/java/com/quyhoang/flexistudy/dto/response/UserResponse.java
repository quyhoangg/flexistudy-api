package com.quyhoang.flexistudy.dto.response;

import com.quyhoang.flexistudy.enums.RoleName;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String email;
    String firstName;
    String lastName;
    String address;
    String phone;
    String avatarUrl;
    LocalDate dob;
    boolean noPassword;
    boolean profileCompleted;
    Set<RoleResponse> roles;
}
