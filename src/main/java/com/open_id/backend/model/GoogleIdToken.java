package com.open_id.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleIdToken {

    private String azp;

    private String aud;

    private String sub;

    private String scope;

    private String exp;

    @JsonProperty(value = "expires_in")
    private String expiresIn;

    private String email;

    @JsonProperty(value = "email_verified")
    private String emailVerified;

    @JsonProperty(value = "access_type")
    private String accessType;
}
