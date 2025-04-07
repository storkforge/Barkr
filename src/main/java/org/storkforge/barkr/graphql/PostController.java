package org.storkforge.barkr.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.entity.Post;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @QueryMapping("Posts")
    public List<Post> posts() {
        return postService.findAll();
    }

    @QueryMapping("Post")
    public Post post(@Argument Long id) {
        return postService.findById(id);
    }


}
