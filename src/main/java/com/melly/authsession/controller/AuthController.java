package com.melly.authsession.controller;

import com.melly.authsession.common.controller.ResponseController;
import com.melly.authsession.common.dto.ResponseDto;
import com.melly.authsession.dto.request.LoginRequestDto;
import com.melly.authsession.dto.response.LoginResponseDto;
import com.melly.authsession.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements ResponseController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto,
                                                               HttpServletRequest httpRequest) {
        LoginResponseDto response = authService.login(httpRequest, loginRequestDto);
        return makeResponseEntity(HttpStatus.OK, null, response.getMessage(), response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return makeResponseEntity(HttpStatus.OK, null, "로그아웃 성공", null);
    }
}
