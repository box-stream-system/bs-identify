package com.boxstream.bs_identity.mapper;

import com.boxstream.bs_identity.dto.request.PermissionRequest;
import com.boxstream.bs_identity.dto.response.PermissionResponse;
import com.boxstream.bs_identity.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionRequest toPermissionRequest(Permission permission);
    PermissionResponse toPermissionResponse(Permission permission);
}
