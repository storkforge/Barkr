package org.storkforge.barkr.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.postDto.ResponsePost;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public Page<ResponsePost> getPosts(@PageableDefault Pageable pageable) {
        return postService.findAll(pageable);
    }

    @GetMapping("/posts/{id}")
    public ResponsePost getPost(@PathVariable Long id) {
        return postService.findById(id);
    }
}
