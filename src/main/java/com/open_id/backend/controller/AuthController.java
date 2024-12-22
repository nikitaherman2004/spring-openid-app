package com.open_id.backend.controller;

import com.open_id.backend.dto.response.SubjectRoleDto;
import com.open_id.backend.service.auth.OidcSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class AuthController {

    private final OidcSecurityService oidcSecurityService;

    @GetMapping("/current")
    public SubjectRoleDto getAuthedUserSubjectRole() {
        return oidcSecurityService.getAuthedUserSubjectRole();
    }
}

