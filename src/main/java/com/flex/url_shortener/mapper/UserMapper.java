package com.flex.url_shortener.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.flex.url_shortener.dto.UserRequest;
import com.flex.url_shortener.dto.UserResponse;
import com.flex.url_shortener.entity.User;
import com.flex.url_shortener.security.UserDetailsImpl;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface UserMapper extends BaseMapper<User, UserRequest, UserResponse> {

    UserResponse toResponse(UserDetailsImpl userDetails);
}
