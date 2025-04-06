package org.storkforge.barkr.domain.services.api;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.exceptions.PostNotFound;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.storkforge.barkr.mapper.PostMapper;

import java.util.List;
import java.util.Objects;

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
        if (posts == null || posts.isEmpty()) {
            throw new PostNotFound("No posts records found in database");
        }
        return posts.stream().filter(Objects::nonNull).map(PostMapper::mapToResponse).toList();
    }


    @ExceptionHandler(PostNotFound.class)
    @ResponseBody
    public ResponsePost findOne(Long id) {
        log.info("Finding post by id: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFound("The post with: " + id + " could not be found"));
        return PostMapper.mapToResponse(post);
    }
}
