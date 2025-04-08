package org.storkforge.barkr.domain.services.api;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.exceptions.PostNotFound;
import org.storkforge.barkr.web.infrastructure.persistence.PostRepository;
import org.storkforge.barkr.mapper.PostMapper;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ApiPostService {

    private final Logger log = LoggerFactory.getLogger(ApiPostService.class);

    private final PostRepository postRepository;

    public ApiPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<ResponsePost> findAll() {
        var posts = postRepository.findAll();

        if (posts.isEmpty()) {
            throw new PostNotFound("No post record(s) found in database");
        }

        log.info("Finding all posts");
        return posts.stream().filter(Objects::nonNull).map(PostMapper::mapToResponse).toList();
    }


    public ResponsePost findOne(Long id) {
        log.info("Finding post by id: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFound("The post with id: " + id + " could not be found"));
        return PostMapper.mapToResponse(post);
    }
}
