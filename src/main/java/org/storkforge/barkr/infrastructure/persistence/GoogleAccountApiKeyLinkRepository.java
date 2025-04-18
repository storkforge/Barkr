package org.storkforge.barkr.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;

import java.util.Optional;

public interface GoogleAccountApiKeyLinkRepository extends JpaRepository<GoogleAccountApiKeyLink, Long> {
    @Override
    Optional<GoogleAccountApiKeyLink> findById(Long id);
}
