package com.open_id.backend.controller;

import com.open_id.backend.dto.filter.AppUserFilter;
import com.open_id.backend.dto.response.AppUserResponseDto;
import com.open_id.backend.service.user.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final AppUserService appUserService;

    @GetMapping("/current")
    public AppUserResponseDto getCurrentAppUser() {
        return appUserService.getCurrentAppUser();
    }

    @GetMapping("/exists")
    public Boolean existsByUserSub(@RequestParam String sub) {
        return appUserService.existsByUserSub(sub);
    }

    @GetMapping
    public Page<AppUserResponseDto> getAppUsersByFilter(@ModelAttribute AppUserFilter filter, Pageable pageable) {
        return appUserService.getAppUsersByFilter(filter, pageable);
    }
}
