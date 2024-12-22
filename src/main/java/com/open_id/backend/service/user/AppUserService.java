package com.open_id.backend.service.user;

import com.open_id.backend.builder.SpecificationBuilder;
import com.open_id.backend.dto.filter.AppUserFilter;
import com.open_id.backend.dto.response.AppUserResponseDto;
import com.open_id.backend.entity.AppUser;
import com.open_id.backend.entity.UserDetails;
import com.open_id.backend.entity.UserRole;
import com.open_id.backend.exception.ApplicationException;
import com.open_id.backend.mapper.AppUserMapper;
import com.open_id.backend.model.OAuth2UserAttributeAccessor;
import com.open_id.backend.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.open_id.backend.enums.EUserRole.*;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private static final String DEFAULT_ROLE_NAME = USER.getValue();

    private final AppUserMapper appUserMapper = AppUserMapper.INSTANCE;

    private final SpecificationBuilder specificationBuilder = new SpecificationBuilder();

    private final UserRoleService userRoleService;

    private final AppUserRepository appUserRepository;

    private final OAuth2UserAttributeAccessor attributeAccessor;

    public Page<AppUserResponseDto> getAppUsersByFilter(AppUserFilter filter, Pageable pageable) {
        Specification<AppUser> specification = specificationBuilder
                .andEqual(filter.getFamilyName(), "familyName")
                .andEqual(filter.getName(), "name")
                .build();

        return appUserRepository.findAll(specification, pageable)
                .map(appUserMapper::toResponseDto);
    }

    public AppUserResponseDto getCurrentAppUser(String sub) {
        Optional<AppUser> optional = appUserRepository.findById(sub);

        if (optional.isPresent()) {
            AppUser appUser = optional.get();

            return appUserMapper.toResponseDto(appUser);
        } else {
            throw new ApplicationException("Аккаунт пользователя не найден", HttpStatus.NOT_FOUND);
        }
    }

    public void createOrUpdateUserAppUser(OAuth2User principal) {
        Optional<AppUser> optional = appUserRepository.findById(attributeAccessor.getSub(principal));

        if (optional.isPresent()) {
            AppUser storedUser = optional.get();

            syncAppUserPersonalInfoWithPrincipal(storedUser, principal);
        } else {
            createNewAppUserFromPrincipal(principal);
        }
    }

    private void syncAppUserPersonalInfoWithPrincipal(AppUser storedUser, OAuth2User principal) {
        mergeUserWithUserDetails(storedUser, storedUser.getUserDetails(), principal);

        appUserRepository.save(storedUser);
    }

    private void createNewAppUserFromPrincipal(OAuth2User principal) {
        AppUser appUser = new AppUser();
        UserDetails userDetails = new UserDetails();

        mergeUserWithUserDetails(appUser, userDetails, principal);

        appUser.setSub(attributeAccessor.getSub(principal));

        UserRole userRole = userRoleService.getUserRoleByName(DEFAULT_ROLE_NAME);
        appUser.setRole(userRole);

        userDetails.setUser(appUser);
        appUser.setUserDetails(userDetails);

        appUserRepository.save(appUser);
    }

    public void mergeUserWithUserDetails(AppUser appUser, UserDetails userDetails, OAuth2User principal) {
        appUser.setName(attributeAccessor.getName(principal));
        appUser.setGivenName(attributeAccessor.getGivenName(principal));
        appUser.setFamilyName(attributeAccessor.getFamilyName(principal));

        userDetails.setEmail(attributeAccessor.getEmail(principal));
        userDetails.setPicture(attributeAccessor.getPicture(principal));
        userDetails.setPicture(attributeAccessor.getPicture(principal));
    }
}
