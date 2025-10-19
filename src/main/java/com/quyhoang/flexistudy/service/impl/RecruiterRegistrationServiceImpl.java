package com.quyhoang.flexistudy.service.impl;

import com.quyhoang.flexistudy.dto.request.RecruiterRegistrationRequest;
import com.quyhoang.flexistudy.dto.response.RecruiterResponse;
import com.quyhoang.flexistudy.entity.Company;
import com.quyhoang.flexistudy.entity.Role;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.mapper.RecruiterMapper;
import com.quyhoang.flexistudy.repository.CompanyRepository;
import com.quyhoang.flexistudy.repository.RoleRepository;
import com.quyhoang.flexistudy.repository.UserRepository;
import com.quyhoang.flexistudy.service.RecruiterRegistrationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecruiterRegistrationServiceImpl implements RecruiterRegistrationService {
    UserRepository userRepository;
    CompanyRepository companyRepository;
    PasswordEncoder passwordEncoder;
    RecruiterMapper recruiterMapper;
    RoleRepository roleRepository;

    @Override
    public RecruiterResponse registerRecruiter(RecruiterRegistrationRequest req) {
        // Kiểm tra email
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Kiểm tra trùng tên công ty
        if (companyRepository.existsByName(req.getCompanyName())) {
            throw new AppException(ErrorCode.COMPANY_ALREADY_EXISTS);
        }

        var recruiterRole = roleRepository.findByName(RoleName.RECRUITER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // 1 Tạo user
        User user = User.builder()
                .email(req.getEmail())
                .username(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .fullName(req.getFirstName() + " " + req.getLastName())
                .roles(new HashSet<>(List.of(recruiterRole)))
                .build();
        user = userRepository.save(user);

        // 2 Tạo công ty
        Company company = Company.builder()
                .name(req.getCompanyName())
                .description(req.getDescription())
                .website(req.getWebsite())
                .memberNumber(req.getMemberNumber() != null ? req.getMemberNumber() : 0)
                .build();
        company = companyRepository.save(company);

        // Gán company cho user
        user.setCompany(company);
        userRepository.save(user);

        // 3 Map DTO trả về
        return recruiterMapper.toRecruiterResponse(user, company);
    }
}
