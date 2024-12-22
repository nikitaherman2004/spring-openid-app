package com.open_id.backend.service.user;

import com.open_id.backend.entity.UserRole;
import com.open_id.backend.exception.ApplicationException;
import com.open_id.backend.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRole getUserRoleByName(String name) {
        Optional<UserRole> optional = userRoleRepository.findByName(name);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw throwApplicationExceptionNotFound(String.format(
                    "Не удалось найти роль пользователя по имени роли %s",
                    name
            ));
        }
    }

    public String getUserRoleBySub(String sub) {
        Optional<UserRole> optional = userRoleRepository.findByUser_Sub(sub);

        if (optional.isPresent()) {
            UserRole userRole = optional.get();

            return userRole.getName();
        } else {
            throw throwApplicationExceptionNotFound(String.format(
                    "Не удалось найти роль пользователя по sub %s пользователя",
                    sub
            ));
        }
    }

    private ApplicationException throwApplicationExceptionNotFound(String message) {
        return new ApplicationException(message, HttpStatus.NOT_FOUND);
    }
}