package com.quyhoang.flexistudy.service;

import com.quyhoang.flexistudy.dto.request.RoleRequest;
import com.quyhoang.flexistudy.dto.response.RoleResponse;
import com.quyhoang.flexistudy.enums.RoleName;
import com.quyhoang.flexistudy.mapper.RoleMapper;
import com.quyhoang.flexistudy.repository.PermissionRepository;
import com.quyhoang.flexistudy.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(RoleName roleName) {
        roleRepository.deleteByName(roleName);
    }
}
