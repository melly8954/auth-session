package com.melly.authsession.service;

import com.melly.authsession.dto.request.LoginRequestDto;
import com.melly.authsession.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponseDto login(HttpServletRequest httpRequest, LoginRequestDto dto);
    void logout(HttpServletRequest request, HttpServletResponse response);
}
