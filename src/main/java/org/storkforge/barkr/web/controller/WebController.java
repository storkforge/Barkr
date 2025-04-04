package org.storkforge.barkr.web.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.CreatePost;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

  private final PostService postService;
  private final AccountService accountService;

  public WebController(PostService postService, AccountService accountService) {
    this.postService = postService;
    this.accountService = accountService;
  }

  @GetMapping("/")
  public String index(Model model) {
    List<Post> posts = postService.findAll();
    model.addAttribute("posts", posts);
    model.addAttribute("createPostDto", new CreatePost("", 1L));
    // TODO: Change this to the actual account once security is in place
    model.addAttribute("account", accountService.findById(1L));

    return "index";
  }

  @GetMapping("/{username}")
  public String user(@PathVariable("username") String username, Model model) {
    ResponseAccount queryAccount = accountService.findByUsername(username);
    if (queryAccount == null) {
      return "redirect:/";
    }

    model.addAttribute("accountPosts", postService.findByUsername(username));
    model.addAttribute("queryAccount", queryAccount);
    // TODO: Change this to the actual account once security is in place
    model.addAttribute("account", accountService.findById(1L));

    return "profile";
  }

  @PostMapping("/post/add")
  public String addPost(@ModelAttribute CreatePost dto,
                        RedirectAttributes redirectAttributes) {
    postService.addPost(dto);

    redirectAttributes.addFlashAttribute("success", true);
    redirectAttributes.addFlashAttribute("createPostDto", new CreatePost("", dto.accountId()));

    return "redirect:/";
  }
}

