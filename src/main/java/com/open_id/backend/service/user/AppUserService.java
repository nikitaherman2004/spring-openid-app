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
import com.open_id.backend.model.OidcAuthenticationToken;
import com.open_id.backend.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Boolean existsByUserSub(String sub) {
        return appUserRepository.existsById(sub);
    }

    public void saveNewAppUser(AppUser storedUser) {
        appUserRepository.save(storedUser);
    }

    public Page<AppUserResponseDto> getAppUsersByFilter(AppUserFilter filter, Pageable pageable) {
        Specification<AppUser> specification = specificationBuilder
                .andEqual(filter.getFamilyName(), "familyName")
                .andEqual(filter.getName(), "name")
                .build();

        return appUserRepository.findAll(specification, pageable)
                .map(appUserMapper::toResponseDto);
    }

    /**
     * The method takes the current user authentication
     * (Exclusively the implementation of the {@link  OidcAuthenticationToken})
     * and retrieves a unique subject from the database of the user associated with this subject.
     */
    public AppUserResponseDto getCurrentAppUser() {
        OidcAuthenticationToken authenticationToken = (OidcAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        Optional<AppUser> optional = appUserRepository.findById(authenticationToken.getSub());

        if (optional.isPresent()) {
            AppUser appUser = optional.get();

            return appUserMapper.toResponseDto(appUser);
        } else {
            throw new ApplicationException("Ошибка при загрузке вашего аккаунта", HttpStatus.NOT_FOUND);
        }
    }

    public Optional<AppUser> findAppUserBySub(String sub) {
        return appUserRepository.findById(sub);
    }

    /**
     * The method synchronizes the account data with the server data.
     * For new users, creates a default user based on attributes
     * from the principal or updates attributes to an existing user
     */
    public void createOrUpdateAppUser(OAuth2User principal) {
        Optional<AppUser> optional = findAppUserBySub(attributeAccessor.getSub(principal));

        if (optional.isPresent()) {
            AppUser storedUser = optional.get();

            updateAppUserFromPrincipal(storedUser, principal);
        } else {
            createAppUserFromPrincipal(principal);
        }
    }

    public void updateAppUserFromPrincipal(AppUser storedUser, OAuth2User principal) {
        syncPrincipalAttributesWithAppUser(storedUser, storedUser.getUserDetails(), principal);
        saveNewAppUser(storedUser);
    }

    /**
     * The method creates a new user of the application and assigns him a standard role for all new users
     */
    private void createAppUserFromPrincipal(OAuth2User principal) {
        AppUser appUser = new AppUser();
        UserDetails userDetails = new UserDetails();

        syncPrincipalAttributesWithAppUser(appUser, userDetails, principal);
        setAttributesForNewAppUser(appUser, userDetails, principal);

        saveNewAppUser(appUser);
    }

    public void setAttributesForNewAppUser(AppUser appUser, UserDetails userDetails, OAuth2User principal) {
        appUser.setSub(attributeAccessor.getSub(principal));

        UserRole userRole = userRoleService.findUserRoleByName(DEFAULT_ROLE_NAME);
        appUser.setRole(userRole);

        userDetails.setUser(appUser);
        appUser.setUserDetails(userDetails);
    }

    public void syncPrincipalAttributesWithAppUser(AppUser appUser, UserDetails userDetails, OAuth2User principal) {
        appUser.setName(attributeAccessor.getName(principal));
        appUser.setGivenName(attributeAccessor.getGivenName(principal));
        appUser.setFamilyName(attributeAccessor.getFamilyName(principal));

        userDetails.setEmail(attributeAccessor.getEmail(principal));
        userDetails.setPicture(attributeAccessor.getPicture(principal));
        userDetails.setPicture(attributeAccessor.getPicture(principal));
    }
}
