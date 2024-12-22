package com.open_id.backend.model;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class OAuth2UserAttributeAccessor {

    private static final String SUB = "sub";

    private static final String NAME = "name";

    private static final String EMAIL = "email";

    private static final String PICTURE = "picture";

    private static final String GIVEN_NAME = "given_name";

    private static final String FAMILY_NAME = "family_name";

    private String getAttribute(String attributeName, OAuth2User principal) {
        return principal.getAttribute(attributeName);
    }

    public String getSub(OAuth2User principal) {
        return getAttribute(SUB, principal);
    }

    public String getName(OAuth2User principal) {
        return getAttribute(NAME, principal);
    }

    public String getEmail(OAuth2User principal) {
        return getAttribute(EMAIL, principal);
    }

    public String getPicture(OAuth2User principal) {
        return getAttribute(PICTURE, principal);
    }

    public String getGivenName(OAuth2User principal) {
        return getAttribute(GIVEN_NAME, principal);
    }

    public String getFamilyName(OAuth2User principal) {
        return getAttribute(FAMILY_NAME, principal);
    }
}
