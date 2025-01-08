package com.boxstream.bs_identity.service;

import java.util.HashSet;
import java.util.List;

import com.boxstream.bs_identity.dto.request.RoleRequest;
import com.boxstream.bs_identity.dto.response.RoleResponse;
import com.boxstream.bs_identity.mapper.RoleMapper;
import com.boxstream.bs_identity.repository.PermissionRepository;
import com.boxstream.bs_identity.repository.RoleRepository;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        //TODO: the permission in RoleRequest must exists in DB
        // When create a new role or update a role
        // User must choose available permissions

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}