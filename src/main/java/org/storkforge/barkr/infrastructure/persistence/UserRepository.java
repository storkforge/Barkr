package org.storkforge.barkr.infrastructure.persistence;

import org.springframework.data.repository.ListCrudRepository;
import org.storkforge.barkr.domain.entity.User;

public interface UserRepository extends ListCrudRepository<User, Long> { }
