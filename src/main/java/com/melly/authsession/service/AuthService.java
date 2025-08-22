package com.melly.authsession.service;

import com.melly.authsession.dto.request.LoginRequestDto;
import com.melly.authsession.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResponseDto login(HttpServletRequest httpRequest, LoginRequestDto dto);
}
