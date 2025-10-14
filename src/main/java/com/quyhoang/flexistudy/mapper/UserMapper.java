package com.quyhoang.flexistudy.mapper;

import com.quyhoang.flexistudy.dto.request.UserCreationRequest;
import com.quyhoang.flexistudy.dto.request.UserUpdateRequest;
import com.quyhoang.flexistudy.dto.response.PermissionResponse;
import com.quyhoang.flexistudy.dto.response.RoleResponse;
import com.quyhoang.flexistudy.dto.response.UserResponse;
import com.quyhoang.flexistudy.entity.Permission;
import com.quyhoang.flexistudy.entity.Role;
import com.quyhoang.flexistudy.entity.User;
import com.quyhoang.flexistudy.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {


    User toUser(UserCreationRequest request);

    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);

    RoleResponse toRoleResponse(Role role);

    PermissionResponse toPermissionResponse(Permission permission);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateNonPasswordFields(@MappingTarget User user, UserUpdateRequest dto);
}


