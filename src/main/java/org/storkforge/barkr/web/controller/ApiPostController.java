package org.storkforge.barkr.web.controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.entity.Post;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Post> getPosts(Model model) {
        return postService.findAll();
    }

    @GetMapping("/posts/{id}")
    public Post getPost(@PathVariable Long id) {
        return postService.findOne(id);
    }


}
