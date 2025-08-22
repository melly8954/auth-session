package com.melly.authsession.service;

import com.melly.authsession.common.enums.ErrorType;
import com.melly.authsession.common.excception.CustomException;
import com.melly.authsession.domain.entity.UserEntity;
import com.melly.authsession.domain.enums.UserRole;
import com.melly.authsession.domain.enums.UserStatus;
import com.melly.authsession.domain.repository.UserRepository;
import com.melly.authsession.dto.request.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(SignUpRequestDto dto) {
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new CustomException(ErrorType.DUPLICATE_USERNAME);
        }

        if(userRepository.existsByEmail(dto.getEmail())){
            throw new CustomException(ErrorType.DUPLICATE_EMAIL);
        }

        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new CustomException(ErrorType.PASSWORD_MISMATCH);
        }

        UserEntity userEntity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(userEntity);
    }
}
