package com.open_id.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AppUserResponseDto {

    private String sub;

    private String name;

    private String givenName;

    private String familyName;

    private UserRoleResponseDto userRole;

    private UserDetailsResponseDto userDetails;
}
