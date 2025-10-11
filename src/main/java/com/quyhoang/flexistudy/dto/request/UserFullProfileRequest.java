package com.quyhoang.flexistudy.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFullProfileRequest {
    @NotBlank
    private String fullName;
    @Email
    private String email;
    @Size(min = 10, max = 15)
    private String phone;

    @Past(message = "Ngày sinh phải là trong quá khứ")
    private LocalDate dob;

    @Size(max = 255, message = "Địa chỉ quá dài")
    private String address;

    private List<EducationDTO> educations;
    private List<ExperienceDTO> experiences;
    private List<String> skills;

    // DTO con nội bộ
    @Data
    public static class EducationDTO {
        private String school;
        private String degree;
        private String field;
    }

    @Data
    public static class ExperienceDTO {
        private String company;
        private String position;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
