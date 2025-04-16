package org.storkforge.barkr.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
  Page<Post> findByAccount(Account account, Pageable pageable);
}
