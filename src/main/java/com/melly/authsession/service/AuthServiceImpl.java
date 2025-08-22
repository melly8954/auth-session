package com.melly.authsession.service;

import com.melly.authsession.common.enums.ErrorType;
import com.melly.authsession.common.excception.CustomException;
import com.melly.authsession.dto.request.LoginRequestDto;
import com.melly.authsession.dto.response.LoginResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponseDto login(HttpServletRequest httpRequest, LoginRequestDto dto) {
        try{
            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(authToken);

            // SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션 생성 및 SecurityContext 저장
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            // 권한 가져오기
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse(null);

            return LoginResponseDto.builder()
                    .username(authentication.getName())
                    .role(role)
                    .message("로그인 성공")
                    .success(true)
                    .build();
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorType.BAD_CREDENTIALS);
        } catch (DisabledException e) {
            if ("USER_DELETED".equals(e.getMessage())) {
                throw new CustomException(ErrorType.USER_DELETED);
            }
            throw new CustomException(ErrorType.USER_INACTIVE);
        } catch (AuthenticationException e) {
            throw new CustomException(ErrorType.INTERNAL_ERROR);
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // SecurityContextHolder 초기화
        SecurityContextHolder.clearContext();

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 쿠키 삭제
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }
}
