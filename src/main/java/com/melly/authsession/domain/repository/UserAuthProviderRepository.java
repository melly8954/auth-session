package com.melly.authsession.domain.repository;

import com.melly.authsession.domain.entity.UserAuthProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProviderEntity, Long> {
}
