package com.melly.authsession.service;

import com.melly.authsession.common.auth.PrincipalDetails;
import com.melly.authsession.common.enums.ErrorType;
import com.melly.authsession.common.excception.CustomException;
import com.melly.authsession.domain.entity.UserAuthProviderEntity;
import com.melly.authsession.domain.entity.UserEntity;
import com.melly.authsession.domain.repository.UserAuthProviderRepository;
import com.melly.authsession.dto.response.OAuth2LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserAuthProviderRepository userAuthProviderRepository;

    @Override
    public OAuth2LoginResponseDto loginWithOAuth(PrincipalDetails principal, HttpServletRequest request, String registrationId) {
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        UserEntity user = principal.getUserEntity();
        UserAuthProviderEntity authProvider = userAuthProviderRepository
                .findByUserIdAndProviderFetchJoin(user.getUserId(), registrationId)
                .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        return OAuth2LoginResponseDto.builder()
                .username(user.getUsername())
                .role(user.getRole().name())
                .message("소셜 로그인 성공")
                .success(true)
                .provider(authProvider.getProvider())
                .providerId(authProvider.getProviderId())
                .build();
    }
}
