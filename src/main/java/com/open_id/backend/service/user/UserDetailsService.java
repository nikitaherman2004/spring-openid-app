package com.open_id.backend.service.user;

import com.open_id.backend.dto.update.UserDetailsUpdateDto;
import com.open_id.backend.entity.UserDetails;
import com.open_id.backend.exception.ApplicationException;
import com.open_id.backend.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    public UserDetails findDetailsById(Long id) {
        Optional<UserDetails> optional = userDetailsRepository.findById(id);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new ApplicationException("Не удалось найти аккаунт пользователя", HttpStatus.NOT_FOUND);
        }
    }

    public void updateUserDetails(UserDetailsUpdateDto updateDto) {
        UserDetails userDetails = findDetailsById(updateDto.getId());

        userDetails.setCity(updateDto.getCity());
        userDetails.setAboutYourself(updateDto.getAboutYourself());

        userDetailsRepository.save(userDetails);
    }
}
