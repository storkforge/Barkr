package org.storkforge.barkr.web.infrastructure.persistence;

import org.springframework.data.repository.ListCrudRepository;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;

import java.util.List;

public interface PostRepository extends ListCrudRepository<Post, Long> {
  public List<Post> findByAccount(Account account);
}
