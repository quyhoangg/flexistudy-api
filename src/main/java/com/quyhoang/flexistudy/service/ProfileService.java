package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.ProfileUpdateRequest;
import com.quyhoang.flexistudy.dto.response.EducationResponse;
import com.quyhoang.flexistudy.dto.response.ExperienceResponse;
import com.quyhoang.flexistudy.dto.response.ProfileResponse;
import com.quyhoang.flexistudy.dto.response.SkillResponse;
import com.quyhoang.flexistudy.entity.Education;
import com.quyhoang.flexistudy.entity.Experience;
import com.quyhoang.flexistudy.entity.Skill;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.ProfileMapper;
import com.quyhoang.flexistudy.repository.EducationRepository;
import com.quyhoang.flexistudy.repository.ExperienceRepository;
import com.quyhoang.flexistudy.repository.SkillRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    UserRepository userRepo;
    EducationRepository eduRepo;
    ExperienceRepository expRepo;
    SkillRepository skillRepo;
    ProfileMapper mapper;

    public ProfileResponse getProfile(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<EducationResponse> educations = eduRepo.findByUserId(userId)
                .stream().map(mapper::toEducationResponse).toList();

        List<ExperienceResponse> experiences = expRepo.findByUserId(userId)
                .stream().map(mapper::toExperienceResponse).toList();

        List<SkillResponse> skills = user.getSkills() != null
                ? user.getSkills().stream().map(mapper::toSkillResponse).toList()
                : List.of();

        return ProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .dob(user.getDob())
                .educations(educations)
                .experiences(experiences)
                .skills(skills)
                .build();
    }


    @Transactional
    public ProfileResponse updateProfile(String userId, ProfileUpdateRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Cập nhật thông tin cơ bản
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        user.setDob(req.getDob());
        user.setAvatarUrl(req.getAvatarUrl());
        userRepo.save(user);

        // Xóa hết và lưu lại Education/Experience mới
        eduRepo.deleteAll(eduRepo.findByUserId(userId));
        expRepo.deleteAll(expRepo.findByUserId(userId));

        if (req.getEducations() != null) {
            req.getEducations().forEach(e -> {
                Education entity = mapper.toEducationEntity(e);
                entity.setUserId(userId);
                eduRepo.save(entity);
            });
        }

        if (req.getExperiences() != null) {
            req.getExperiences().forEach(e -> {
                Experience entity = mapper.toExperienceEntity(e);
                entity.setUserId(userId);
                expRepo.save(entity);
            });
        }

        if (req.getSkillIds() != null) {
            Set<Skill> newSkills = new HashSet<>(skillRepo.findAllById(req.getSkillIds()));
            user.setSkills(newSkills);
        }

        return getProfile(userId);
    }
}
