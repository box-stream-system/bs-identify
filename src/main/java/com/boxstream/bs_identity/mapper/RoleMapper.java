package com.boxstream.bs_identity.mapper;

import com.boxstream.bs_identity.dto.request.RoleRequest;
import com.boxstream.bs_identity.dto.response.RoleResponse;
import com.boxstream.bs_identity.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
