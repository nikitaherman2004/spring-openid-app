package com.open_id.backend.controller;

import com.open_id.backend.dto.update.UserDetailsUpdateDto;
import com.open_id.backend.service.user.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-details")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @PutMapping
    public void updateUserDetails(@RequestBody UserDetailsUpdateDto updateDto) {
        userDetailsService.updateUserDetails(updateDto);
    }
}
