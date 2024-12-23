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

    public void saveUserDetails(UserDetails userDetails) {
        userDetailsRepository.save(userDetails);
    }

    public UserDetails findUserDetailsById(Long id) {
        Optional<UserDetails> optional = userDetailsRepository.findById(id);

        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new ApplicationException(
                    String.format("Не удалось найти Информацию о пользователю по id - %d", id),
                    HttpStatus.NOT_FOUND);
        }
    }

    public void updateUserDetails(UserDetailsUpdateDto updateDto) {
        UserDetails userDetails = findUserDetailsById(updateDto.getId());

        userDetails.setCity(updateDto.getCity());
        userDetails.setAboutYourself(updateDto.getAboutYourself());

        saveUserDetails(userDetails);
    }
}
