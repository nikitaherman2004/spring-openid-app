package com.open_id.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsResponseDto {

    private Long id;

    private String city;

    private String email;

    private String picture;

    private String dateOfBirth;

    private String aboutYourself;
}
