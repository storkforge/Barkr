package org.storkforge.barkr.domain;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.storkforge.barkr.mapper.PostMapper;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final Logger log = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<ResponsePost> findAll() {
        log.info("Finding all posts");

        var posts = postRepository.findAll();
        return posts.stream().map(PostMapper::mapToResponse).toList();
    }


    public ResponsePost findOne(Long id) {
        log.info("Finding post by id: {}", id);
        Post post = postRepository.findById(id).orElse(null);
        return PostMapper.mapToResponse(post);
    }
}
