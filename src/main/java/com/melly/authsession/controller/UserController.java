package com.melly.authsession.controller;

import com.melly.authsession.common.controller.ResponseController;
import com.melly.authsession.common.dto.ResponseDto;
import com.melly.authsession.dto.request.SignUpRequestDto;
import com.melly.authsession.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements ResponseController {
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<ResponseDto<Void>> signUp(@RequestBody SignUpRequestDto dto){
        userService.signUp(dto);
        return makeResponseEntity(HttpStatus.OK, null, "회원가입 성공", null);
    }

}
