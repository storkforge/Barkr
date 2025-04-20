package org.storkforge.barkr.infrastructure.persistence;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.entity.IssuedApiKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssuedApiKeyRepository extends JpaRepository<IssuedApiKey, Long> {
    @Override
    Optional<IssuedApiKey> findById(Long id);

    Optional<IssuedApiKey> findByHashedApiKey(String hashedApiKey);

    List<IssuedApiKey> findByGoogleAccountApiKeyLinkOrderByIssuedAtDesc(@NotNull GoogleAccountApiKeyLink googleAccountApiKeyLink);

    Optional<IssuedApiKey> findByReferenceId(@NotNull UUID referenceId);

    @Modifying
    @Query("UPDATE IssuedApiKey k SET k.revoked = true WHERE k.expiresAt <= :now AND k.revoked = false")
    int revokeExpiredKeys(@Param("now") LocalDateTime now);

}
