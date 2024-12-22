package com.open_id.backend.dto.update;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsUpdateDto {

    private Long id;

    private String city;

    private String aboutYourself;
}
