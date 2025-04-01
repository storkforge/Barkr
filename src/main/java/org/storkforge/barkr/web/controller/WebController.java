package org.storkforge.barkr.web.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.entity.Post;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

  private final PostService postService;

  public WebController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/")
  public String index(Model model) {
    List<Post> posts = postService.findAll();
    model.addAttribute("posts", posts);
    return "index";
  }


}

