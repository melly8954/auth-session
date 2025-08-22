package com.melly.authsession.service;

import com.melly.authsession.common.auth.PrincipalDetails;
import com.melly.authsession.dto.response.OAuth2LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface OAuth2LoginService {
    OAuth2LoginResponseDto loginWithOAuth(PrincipalDetails principal, HttpServletRequest request, String registrationId);
}
