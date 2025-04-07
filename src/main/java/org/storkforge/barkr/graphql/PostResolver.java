package org.storkforge.barkr.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.postDto.ResponsePost;

import java.util.List;

@Controller
public class PostResolver {

    private final PostService postService;

    public PostResolver(PostService postService) {
        this.postService = postService;
    }

    @QueryMapping("Posts")
    public List<ResponsePost> posts() { return postService.findAll(); }

    @QueryMapping("Post")
    public ResponsePost post(@Argument Long id) { return postService.findById(id); }
}
