package org.storkforge.barkr.domain;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import java.util.List;

@Service
@Transactional
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findAll() {
        log.info("Finding all posts");
        return postRepository.findAll();
    }


    public Post findOne(Long id) {
        log.info("Finding post by id: {}", id);
        return postRepository.findById(id).orElse(null);
    }
}
