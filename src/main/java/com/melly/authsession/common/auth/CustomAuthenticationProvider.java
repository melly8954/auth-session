package com.melly.authsession.common.auth;

import com.melly.authsession.common.enums.ErrorType;
import com.melly.authsession.common.excception.CustomException;
import com.melly.authsession.domain.entity.UserEntity;
import com.melly.authsession.domain.enums.UserStatus;
import com.melly.authsession.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();

        // 사용자 조회
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("BAD_CREDENTIALS");
        }

        // 계정 상태 체크
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("USER_INACTIVE");
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new DisabledException("USER_DELETED");
        }

        // 인증 토큰과 권한 생성
        PrincipalDetails principalDetails = new PrincipalDetails(user);

        String roleName = user.getRole().name(); // 예: ADMIN
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + roleName)
        );

        return new UsernamePasswordAuthenticationToken(principalDetails, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
