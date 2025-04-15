package org.storkforge.barkr.graphql;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.postDto.ResponsePost;


@Controller
public class PostResolver {

    private final PostService postService;

    public PostResolver(PostService postService) {
        this.postService = postService;
    }

    @QueryMapping("posts")
    public Page<ResponsePost> posts(@Argument @PositiveOrZero int page, @Argument @Positive int size) {
        Pageable pageable = PageRequest.of(page,size);

        return postService.findAll(pageable);
    }

    @QueryMapping("post")
    public ResponsePost post(@Argument @NotNull @Positive Long id) {
      return postService.findById(id);
    }
}
