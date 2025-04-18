package org.storkforge.barkr.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.storkforge.barkr.domain.entity.IssuedApiKey;

import java.util.Optional;

public interface IssuedApiKeyRepository extends JpaRepository<IssuedApiKey, Long> {
    @Override
    Optional<IssuedApiKey> findById(Long id);

    Optional<IssuedApiKey> findByHashedApiKey(String hashedApiKey);
}
