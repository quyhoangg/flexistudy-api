package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.UserFullProfileRequest;
import com.quyhoang.flexistudy.entity.Education;
import com.quyhoang.flexistudy.entity.Experience;
import com.quyhoang.flexistudy.entity.Skill;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.repository.EducationRepository;
import com.quyhoang.flexistudy.repository.ExperienceRepository;
import com.quyhoang.flexistudy.repository.SkillRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserRepository userRepository;
    EducationRepository educationRepository;
    ExperienceRepository experienceRepository;
    SkillRepository skillRepository;

    // =============================
    // Cập nhật toàn bộ hồ sơ người dùng
    // =============================
    @Transactional
    public void updateFullProfile(String userId, UserFullProfileRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            String[] parts = req.getFullName().trim().split("\\s+");
            String lastName = parts[0];
            String firstName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            user.setFirstName(firstName);
            user.setLastName(lastName);
        }

        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getDob() != null) user.setDob(req.getDob());
        if (req.getAddress() != null && !req.getAddress().isBlank()) user.setAddress(req.getAddress());

        user.setProfileCompleted(true);
        userRepository.save(user);

        // Xóa và ghi lại học vấn (educations)
        educationRepository.deleteByUserId(userId);
        if (req.getEducations() != null && !req.getEducations().isEmpty()) {
            List<Education> eduList = req.getEducations().stream()
                    .map(e -> Education.builder()
                            .userId(userId)
                            .school(e.getSchool())
                            .degree(e.getDegree())
                            .field(e.getField())
                            .build())
                    .collect(Collectors.toList());
            educationRepository.saveAll(eduList);
        }

        // Xóa và ghi lại kinh nghiệm (experiences)
        experienceRepository.deleteByUserId(userId);
        if (req.getExperiences() != null && !req.getExperiences().isEmpty()) {
            List<Experience> expList = req.getExperiences().stream()
                    .map(e -> Experience.builder()
                            .userId(userId)
                            .company(e.getCompany())
                            .position(e.getPosition())
                            .startDate(e.getStartDate())
                            .endDate(e.getEndDate())
                            .build())
                    .collect(Collectors.toList());
            experienceRepository.saveAll(expList);
        }

        // Cập nhật kỹ năng (ManyToMany)
        if (req.getSkills() != null) {
            user.getSkills().clear(); // xóa toàn bộ kỹ năng cũ
            for (String skillName : req.getSkills()) {
                Skill skill = skillRepository.findByName(skillName)
                        .orElseGet(() -> skillRepository.save(Skill.builder().name(skillName).build()));
                user.getSkills().add(skill);
            }
        }

        // Lưu lại user để cập nhật ManyToMany
        userRepository.save(user);
    }

    // =============================
    // Lấy toàn bộ hồ sơ người dùng (để FE load form)
    // =============================
    public Map<String, Object> getFullProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("user", user);
        profile.put("educations", educationRepository.findByUserId(userId));
        profile.put("experiences", experienceRepository.findByUserId(userId));

        // Map kỹ năng (chỉ trả về tên kỹ năng)
        List<String> skills = user.getSkills().stream()
                .map(Skill::getName)
                .toList();
        profile.put("skills", skills);

        return profile;
    }
}
