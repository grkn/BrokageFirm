package com.brokage.controller;

import com.brokage.constant.ChallengeConstant;
import com.brokage.dto.UserDto;
import com.brokage.modal.UserEntity;
import com.brokage.resource.TokenResource;
import com.brokage.resource.UserResource;
import com.brokage.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = ChallengeConstant.BASE_URL)
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping(ChallengeConstant.AUTHENTICATE_ENDPOINT)
    public ResponseEntity<UserResource> authenticate(@RequestBody @Valid UserDto userDto) {
        UserEntity user = userService.createUserEntity(UserEntity.builder()
                .password(userDto.getPassword())
                .userName(userDto.getUserName()).build(), userDto.getType());
        return ResponseEntity.ok(UserResource.builder().userName(user.getUserName()).build());
    }

    @PostMapping(ChallengeConstant.TOKEN_ENDPOINT)
    public ResponseEntity<TokenResource> getAccessToken(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(new TokenResource(userService.
                createToken(UserEntity.builder().userName(userDto.getUserName())
                        .password(userDto.getPassword()).build())));
    }
}