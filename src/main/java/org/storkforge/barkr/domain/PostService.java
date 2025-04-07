package org.storkforge.barkr.domain;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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
        return postRepository.findAll()
                .stream()
                .map(PostMapper::mapToResponse)
                .toList();
    }

    public ResponsePost findById(Long id) {
        return postRepository.findById(id)
                .map(PostMapper::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Post with id " + id + " not found"));
    }
}
