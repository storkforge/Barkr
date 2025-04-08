package org.storkforge.barkr.graphql;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @QueryMapping("posts")
    public List<ResponsePost> posts() {
        try {
            return postService.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving posts: " + e.getMessage(), e);
        }
    }

    @QueryMapping("post")
    public ResponsePost post(@Argument @NotNull @Positive Long id) {
        try {
            ResponsePost post = postService.findById(id);
            if (post == null) {
                throw new RuntimeException("Post not found for ID: " + id);
            }
            return post;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving post: " + e.getMessage(), e);
        }
    }
}
