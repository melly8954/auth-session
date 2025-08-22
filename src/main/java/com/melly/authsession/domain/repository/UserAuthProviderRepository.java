package com.melly.authsession.domain.repository;

import com.melly.authsession.domain.entity.UserAuthProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProviderEntity, Long> {
    @Query("select ap from UserAuthProviderEntity ap join fetch ap.user u " +
            "where u.userId = :userId and ap.provider = :provider")
    Optional<UserAuthProviderEntity> findByUserIdAndProviderFetchJoin(@Param("userId") Long userId,
                                                                      @Param("provider") String provider);
}
