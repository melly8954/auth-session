package com.melly.authsession.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuth2LoginResponseDto {
    private String username;
    private String role;
    private String message;
    private boolean success;
    private String provider; // GOOGLE, KAKAO 등
    private String providerId; // OAuth2 유저 고유 ID
}
