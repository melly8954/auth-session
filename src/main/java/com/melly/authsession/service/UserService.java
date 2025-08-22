package com.melly.authsession.service;

import com.melly.authsession.dto.request.SignUpRequestDto;

public interface UserService {
    void signUp(SignUpRequestDto dto);
}
