package com.open_id.backend.mapper;

import com.open_id.backend.dto.response.UserRoleResponseDto;
import com.open_id.backend.entity.UserRole;
import org.mapstruct.Mapper;

@Mapper
public interface UserRoleMapper {

    UserRoleResponseDto toResponseDto(UserRole userRole);
}
