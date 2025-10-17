package com.quyhoang.flexistudy.controller;

import com.quyhoang.flexistudy.dto.ApiResponse;
import com.quyhoang.flexistudy.dto.PageResponse;
import com.quyhoang.flexistudy.dto.request.*;
import com.quyhoang.flexistudy.dto.response.AuthenticationResponse;
import com.quyhoang.flexistudy.dto.response.UserResponse;
import com.quyhoang.flexistudy.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.<Void>builder()
                .message("Đăng ký tài khoản thành công!")
                .build();
    }

    @PostMapping("/create-password")
    ApiResponse<AuthenticationResponse> createPassword(@RequestBody @Valid PasswordCreationRequest request) {
        var result = userService.createPassword(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .message("Password has been created, you could use it to log-in")
                .build();
    }


    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email
    ) {
        PageResponse<UserResponse> response = userService.getAllUsers(page, size, search, username, email);
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(response)
                .build();
    }



    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyinfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfor())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

    @PostMapping("/avatar/{userId}")
    public ApiResponse<String> uploadAvatar(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file
    ) {
        String fileUrl = userService.uploadAvatar(userId, file);
        return ApiResponse.<String>builder()
                .result(fileUrl)
                .message("Avatar uploaded successfully")
                .build();
    }

    @PostMapping("/skills/{userId}")
    public ApiResponse<Void> addUserSkills(
            @PathVariable String userId,
            @RequestBody List<String> skills
    ) {
        userService.addSkillsToUser(userId, skills);
        return ApiResponse.<Void>builder()
                .message("Skills updated successfully")
                .build();
    }
}
