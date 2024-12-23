package com.open_id.backend.configuration.security;

import com.open_id.backend.filter.SecurityAuthFilter;
import com.open_id.backend.service.auth.OidcSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityFilterChainConfiguration {

    private static final String JSESSIONID_COOKIE = "JSESSIONID";

    @Value("${post-logout-redirect-uri}")
    private String redirectSuccessUri;

    @Value("${cookie.access-session.name}")
    private String xAuthCookie;

    private final SecurityAuthFilter securityAuthFilter;

    private final OidcSecurityService OidcSecurityService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .anyRequest().authenticated())
                .oauth2Login((configurer) -> configurer
                        .successHandler(this::onAuthorizationSuccess)
                        .failureHandler(this::onAuthenticationFailure))
                .sessionManagement((configurer) -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionFixation().migrateSession()
                        .sessionAuthenticationStrategy(OidcSecurityService))
                .oidcLogout((logout) -> logout
                        .backChannel(Customizer.withDefaults())
                )
                .logout((configurer) -> configurer
                        .deleteCookies(JSESSIONID_COOKIE, xAuthCookie)
                        .invalidateHttpSession(true))
                .addFilterAfter(securityAuthFilter, SecurityContextHolderFilter.class);

        return httpSecurity.build();
    }

    private void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication failed, try again");
    }

    private void onAuthorizationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        response.sendRedirect(redirectSuccessUri);
    }
}