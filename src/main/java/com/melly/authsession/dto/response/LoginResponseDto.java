package com.melly.authsession.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String username;
    private String role;
    private String message;
    private boolean success;
}
