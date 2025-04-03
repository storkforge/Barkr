package org.storkforge.barkr.infrastructure.persistence;

import org.springframework.data.repository.ListCrudRepository;
import org.storkforge.barkr.domain.entity.Post;

public interface PostRepository extends ListCrudRepository<Post, Long> {

}
