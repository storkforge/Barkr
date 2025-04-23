package org.storkforge.barkr.infrastructure.persistence;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.storkforge.barkr.domain.entity.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByUsernameEqualsIgnoreCase(String username);

  @Query("SELECT a.image FROM Account a WHERE a.id = :id")
  Optional<byte[]> getAccountImage(Long id);


  Optional<Account> findByGoogleOidc2Id(@NotBlank String googleOidc2Id);


}
