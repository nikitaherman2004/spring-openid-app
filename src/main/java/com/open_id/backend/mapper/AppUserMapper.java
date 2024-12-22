package com.open_id.backend.mapper;

import com.open_id.backend.dto.response.AppUserResponseDto;
import com.open_id.backend.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UserDetailsMapper.class, UserDetailsMapper.class})
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    AppUserResponseDto toResponseDto(AppUser appUser);
}
