package com.melly.authsession.service;

import com.melly.authsession.common.enums.ErrorType;
import com.melly.authsession.common.excception.CustomException;
import com.melly.authsession.dto.request.LoginRequestDto;
import com.melly.authsession.dto.response.LoginResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl 단위 테스트")
public class AuthServiceImplTest {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private HttpServletRequest httpRequest;
    @Mock private HttpServletResponse httpResponse;
    @Mock private HttpSession httpSession;
    @Mock private Authentication authentication;

    @InjectMocks private AuthServiceImpl authService;

    @Nested
    @DisplayName("login() 메서드 테스트")
    class login {
        @Test
        @DisplayName("성공 - 로그인 성공")
        void login_success() {
            LoginRequestDto dto = new LoginRequestDto("testUser", "password");

            when(httpRequest.getSession(true)).thenReturn(httpSession);
            when(authentication.getName()).thenReturn("testUser");
            doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .when(authentication).getAuthorities();
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);

            LoginResponseDto response = authService.login(httpRequest, dto);

            assertThat(response.getUsername()).isEqualTo("testUser");
            assertThat(response.getRole()).isEqualTo("ROLE_USER");
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("로그인 성공");

            verify(httpSession, times(1))
                    .setAttribute(any(), any());
        }

        @Test
        @DisplayName("예외 - 로그인 비밀번호 불일치")
        void login_badCredentials() {
            LoginRequestDto dto = new LoginRequestDto("testUser", "wrongPassword");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(httpRequest, dto))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.BAD_CREDENTIALS);
        }

        @Test
        @DisplayName("예외 - 사용자 비활성화 (USER_DELETED)")
        void login_disabledUserDeleted() {
            LoginRequestDto dto = new LoginRequestDto("testUser", "password");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new DisabledException("USER_DELETED"));

            assertThatThrownBy(() -> authService.login(httpRequest, dto))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.USER_DELETED);
        }

        @Test
        @DisplayName("예외 - 사용자 비활성화 (USER_INACTIVE)")
        void login_disabledUserInactive() {
            LoginRequestDto dto = new LoginRequestDto("testUser", "password");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new DisabledException("OTHER_REASON"));

            assertThatThrownBy(() -> authService.login(httpRequest, dto))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorType")
                    .isEqualTo(ErrorType.USER_INACTIVE);
        }
    }

    @Nested
    @DisplayName("logout() 메서드 테스트")
    class logout {

        @Test
        @DisplayName("성공 - 로그아웃 시 세션 무효화, SecurityContext 초기화, JSESSIONID 쿠키 삭제")
        void logout_success() {
            // given
            when(httpRequest.getSession(false)).thenReturn(httpSession);
            Cookie jsessionCookie = new Cookie("JSESSIONID", "12345");
            when(httpRequest.getCookies()).thenReturn(new Cookie[]{jsessionCookie});

            // SecurityContext에 임의 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(mock(org.springframework.security.core.Authentication.class));

            // when
            authService.logout(httpRequest, httpResponse);

            // then
            // 세션 무효화 확인
            verify(httpSession).invalidate();

            // SecurityContext 초기화 확인
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

            // 쿠키 삭제 확인
            verify(httpResponse).addCookie(argThat(cookie ->
                    "JSESSIONID".equals(cookie.getName()) &&
                            cookie.getMaxAge() == 0 &&
                            cookie.getValue() == null
            ));
        }

        @Test
        @DisplayName("성공 - 세션이 없는 경우에도 로그아웃 동작")
        void logout_noSession() {
            // given
            when(httpRequest.getSession(false)).thenReturn(null);
            when(httpRequest.getCookies()).thenReturn(null);

            // SecurityContext에 임의 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(mock(org.springframework.security.core.Authentication.class));

            // when
            authService.logout(httpRequest, httpResponse);

            // then
            // SecurityContext 초기화 확인
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            // 세션 관련 메서드 호출 없음을 확인
            verify(httpSession, never()).invalidate();
        }
    }
}
