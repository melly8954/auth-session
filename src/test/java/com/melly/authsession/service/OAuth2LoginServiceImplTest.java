package com.melly.authsession.service;

import com.melly.authsession.common.auth.PrincipalDetails;
import com.melly.authsession.common.enums.ErrorType;
import com.melly.authsession.common.excception.CustomException;
import com.melly.authsession.domain.entity.UserAuthProviderEntity;
import com.melly.authsession.domain.entity.UserEntity;
import com.melly.authsession.domain.enums.UserRole;
import com.melly.authsession.domain.repository.UserAuthProviderRepository;
import com.melly.authsession.dto.response.OAuth2LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2LoginServiceImpl 단위 테스트")
public class OAuth2LoginServiceImplTest {
    @Mock private UserAuthProviderRepository userAuthProviderRepository;
    @Mock private PrincipalDetails principal;
    @Mock private HttpServletRequest request;
    @Mock private HttpSession session;

    @InjectMocks private OAuth2LoginServiceImpl oAuth2LoginService;

    private UserEntity userEntity;
    private UserAuthProviderEntity authProviderEntity;

    @BeforeEach
    void setUp() {
        // 테스트용 UserEntity
        userEntity = UserEntity.builder()
                .userId(1L)
                .username("testuser")
                .role(UserRole.USER) // enum 예시
                .build();

        // 테스트용 UserAuthProviderEntity
        authProviderEntity = UserAuthProviderEntity.builder()
                .provider("google")
                .providerId("google-123")
                .build();
    }

    @Nested
    @DisplayName("loginWithOAuth() 메서드 테스트")
    class LoginWithOAuthTest {

        @Test
        @DisplayName("성공 - OAuth2 로그인 성공")
        void loginWithOAuth_success() {
            // given
            when(principal.getUserEntity()).thenReturn(userEntity);
            when(request.getSession(true)).thenReturn(session);
            when(userAuthProviderRepository.findByUserIdAndProviderFetchJoin(1L, "google"))
                    .thenReturn(Optional.of(authProviderEntity));

            // when
            OAuth2LoginResponseDto response = oAuth2LoginService.loginWithOAuth(principal, request, "google");

            // then
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getUsername()).isEqualTo("testuser");
            assertThat(response.getRole()).isEqualTo("USER");
            assertThat(response.getProvider()).isEqualTo("google");
            assertThat(response.getProviderId()).isEqualTo("google-123");

            verify(session, times(1))
                    .setAttribute(eq("SPRING_SECURITY_CONTEXT"), any(SecurityContext.class));
        }

        @Test
        @DisplayName("예외 - 사용자 없음")
        void loginWithOAuth_userNotFound() {
            // given
            when(principal.getUserEntity()).thenReturn(userEntity);
            when(request.getSession(true)).thenReturn(session);
            when(userAuthProviderRepository.findByUserIdAndProviderFetchJoin(1L, "google"))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> oAuth2LoginService.loginWithOAuth(principal, request, "google"))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorType", ErrorType.USER_NOT_FOUND);
        }
    }
}
