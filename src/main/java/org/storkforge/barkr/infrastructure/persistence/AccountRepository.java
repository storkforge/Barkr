package org.storkforge.barkr.infrastructure.persistence;

import org.springframework.data.repository.ListCrudRepository;
import org.storkforge.barkr.domain.entity.Account;

public interface AccountRepository extends ListCrudRepository<Account, Long> {

}
