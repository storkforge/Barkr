package org.storkforge.barkr.api.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.services.api.ApiPostService;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.dto.postDto.ResponsePostList;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final ApiPostService postService;

    public ApiPostController(ApiPostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public ResponsePostList getPosts() {
        List<ResponsePost> posts = postService.findAll();
       return new ResponsePostList(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponsePost getPost(@PathVariable Long id) {
        return postService.findOne(id);
    }


}
