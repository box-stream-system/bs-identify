package com.boxstream.bs_identity.mapper;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.request.UserUpdateRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.dto.response.UserUpdateResponse;
import com.boxstream.bs_identity.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);
    UserResponse toUserResponse(User user);
    UserUpdateResponse toUserUpdateResponse(User user);

    // auto it will map roles
    // but we build data from request difference with roles in Entity
    // we want to it craft, then just ignore auto mapping
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // auto it will map roles
    // but we build data from request difference with roles in Entity
    // we want to it craft, then just ignore auto mapping
    @Mapping(target = "roles", ignore = true)
    void updateUserFew(@MappingTarget User user, UserUpdateRequest userUpdateRequest);

}
