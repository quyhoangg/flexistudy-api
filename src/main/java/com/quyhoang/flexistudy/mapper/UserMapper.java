package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.request.UserCreationRequest;
import com.quyhoang.flexistudy.dto.request.UserUpdateRequest;
import com.quyhoang.flexistudy.dto.response.PermissionResponse;
import com.quyhoang.flexistudy.dto.response.RoleResponse;
import com.quyhoang.flexistudy.dto.response.UpgradePlanResponse;
import com.quyhoang.flexistudy.dto.response.UserResponse;
import com.quyhoang.flexistudy.entity.Permission;
import com.quyhoang.flexistudy.entity.Role;
import com.quyhoang.flexistudy.entity.UpgradePlan;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Chuyển UserCreationRequest -> User entity
    User toUser(UserCreationRequest request);

    // Chuyển User -> UserResponse (bao gồm roles và upgradePlan)
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "upgradePlan", source = "upgradePlan")
    @Mapping(target = "skills", source = "skills")
    UserResponse toUserResponse(User user);

    // Chuyển Role -> RoleResponse
    @Mapping(target = "permissions", source = "permissions")
    RoleResponse toRoleResponse(Role role);

    // Chuyển Permission -> PermissionResponse
    PermissionResponse toPermissionResponse(Permission permission);

    // Chuyển UpgradePlan -> UpgradePlanResponse
    UpgradePlanResponse toUpgradePlanResponse(UpgradePlan upgradePlan);

    // Update các trường không bao gồm password và roles
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "upgradePlan", ignore = true)
    void updateNonPasswordFields(@MappingTarget User user, UserUpdateRequest dto);

    // --- Helper default methods (nếu cần map thủ công cho Set) ---
    default Set<RoleResponse> mapRoles(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toSet());
    }

    default Set<PermissionResponse> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) return Set.of();
        return permissions.stream()
                .map(this::toPermissionResponse)
                .collect(Collectors.toSet());
    }
}