package org.storkforge.barkr.domain;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import java.util.List;

@Service
@Transactional
public class PostService {

    Logger log = LoggerFactory.getLogger(UserService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        log.info("Finding all posts");
        return postRepository.findAll();
    }
}
