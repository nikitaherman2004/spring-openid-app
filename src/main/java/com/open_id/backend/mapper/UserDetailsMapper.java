package com.open_id.backend.mapper;

import com.open_id.backend.dto.response.UserDetailsResponseDto;
import com.open_id.backend.entity.UserDetails;
import org.mapstruct.Mapper;

@Mapper
public interface UserDetailsMapper {

    UserDetailsResponseDto toResponseDto(UserDetails userDetails);
}
