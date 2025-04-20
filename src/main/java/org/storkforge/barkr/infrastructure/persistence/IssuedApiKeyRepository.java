package org.storkforge.barkr.infrastructure.persistence;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.entity.IssuedApiKey;

import java.util.List;
import java.util.Optional;

public interface IssuedApiKeyRepository extends JpaRepository<IssuedApiKey, Long> {
    @Override
    Optional<IssuedApiKey> findById(Long id);

    Optional<IssuedApiKey> findByHashedApiKey(String hashedApiKey);

    List<IssuedApiKey> findByGoogleAccountApiKeyLink(@NotNull GoogleAccountApiKeyLink googleAccountApiKeyLink);
    
}
