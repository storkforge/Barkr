package org.storkforge.barkr.web.controller;


import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.dto.postDto.ResponsePostList;
import org.storkforge.barkr.mapper.PostMapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public ResponsePostList getPosts(Model model) {
        List<ResponsePost> posts = postService.findAll();
       return new ResponsePostList(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponsePost getPost(@PathVariable Long id) {
        return postService.findOne(id);
    }


}
