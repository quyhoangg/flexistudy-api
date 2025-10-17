package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.PasswordCreationRequest;
import com.quyhoang.flexistudy.dto.request.RegisterRequest;
import com.quyhoang.flexistudy.dto.request.UserCreationRequest;
import com.quyhoang.flexistudy.dto.request.UserUpdateRequest;
import com.quyhoang.flexistudy.dto.response.AuthenticationResponse;
import com.quyhoang.flexistudy.dto.response.UserResponse;
import com.quyhoang.flexistudy.entity.*;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.exception.AppException;
import com.quyhoang.flexistudy.exception.ErrorCode;
import com.quyhoang.flexistudy.mapper.UserMapper;
import com.quyhoang.flexistudy.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    SkillRepository skillRepository;
    FileStorageService fileStorageService;
    AuthenticationService authenticationService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roleEntities = new HashSet<>();
        roleRepository.findByName(RoleName.USER).ifPresent(roleEntities::add);
        user.setRoles(roleEntities);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
    }

    public AuthenticationResponse createPassword(PasswordCreationRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (StringUtils.hasText(user.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_EXISTED);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String token = authenticationService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .email(user.getEmail())
                .build();
    }


    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roleEntities = new HashSet<>();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Dùng trực tiếp danh sách enum từ request
            List<RoleName> roleNames = request.getRoles();

            roleEntities = new HashSet<>(roleRepository.findByNameIn(roleNames));

            if (roleEntities.isEmpty()) {
                throw new AppException(ErrorCode.ROLE_NOT_FOUND);
            }

        } else {
            // Nếu không truyền roles → mặc định USER
            roleRepository.findByName(RoleName.USER).ifPresent(roleEntities::add);
        }

        user.setRoles(roleEntities);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }


    public UserResponse getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));

        return userResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserResponse> getAllUsers(int page, int size, String search, String username, String email) {
        log.info("get all users: page={}, size={}, search={}, username={}, email={}", page, size, search, username, email);

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<User> userPage;

        if (username != null && !username.isBlank()) {
            userPage = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        } else if (email != null && !email.isBlank()) {
            userPage = userRepository.findByEmailContainingIgnoreCase(email, pageable);
        } else if (search != null && !search.isBlank()) {
            userPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserResponse> userResponses = userPage.getContent()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        return PageResponse.<UserResponse>builder()
                .currentPage(userPage.getNumber() + 1)
                .totalPages(userPage.getTotalPages())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .data(userResponses)
                .build();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateNonPasswordFields(user, request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<RoleName> roleNames = request.getRoles();

            Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(roleNames));
            if (roles.isEmpty()) {
                throw new AppException(ErrorCode.ROLE_NOT_FOUND);
            }

            user.setRoles(roles);
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }


    public void deleteUserById(String userId) {
        userRepository.deleteById(userId);
    }

    public String uploadAvatar(String userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String newUrl = fileStorageService.uploadFile(file, "avatars");
        fileStorageService.deleteFile(user.getAvatarUrl());

        user.setAvatarUrl(newUrl);
        userRepository.save(user);

        return newUrl;
    }

    @Transactional
    public void addSkillsToUser(String userId, List<String> skillNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Set<Skill> skills = skillNames.stream()
                .map(name -> skillRepository.findByNameIgnoreCase(name.trim())
                        .orElseGet(() -> skillRepository.save(
                                Skill.builder().name(name.trim()).build()
                        )))
                .collect(Collectors.toSet());

        user.setSkills(skills);
        userRepository.save(user);
    }

    @Transactional
    public void removeSkillFromUser(String userId, String skillName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.getSkills().removeIf(skill ->
                skill.getName().equalsIgnoreCase(skillName));

        userRepository.save(user);
    }
}
