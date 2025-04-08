package org.storkforge.barkr.web.infrastructure.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.storkforge.barkr.domain.entity.Account;

import java.util.Optional;

public interface AccountRepository extends ListCrudRepository<Account, Long> {
  Optional<Account> findByUsernameEqualsIgnoreCase(String username);

  @Query("SELECT a.image FROM Account a WHERE a.id = :id")
  byte[] getAccountImage(Long id);
}
