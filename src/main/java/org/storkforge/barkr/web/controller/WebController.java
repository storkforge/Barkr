package org.storkforge.barkr.web.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.DogFactService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.CreatePost;

import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/")
public class WebController {

  private final PostService postService;
  private final AccountService accountService;
  private final DogFactService dogFactService;

  public WebController(PostService postService, AccountService accountService, DogFactService dogFactService) {
    this.postService = postService;
    this.accountService = accountService;
    this.dogFactService = dogFactService;
  }

  @GetMapping("/")
  public String index(Model model, @PageableDefault Pageable pageable) {
    model.addAttribute("posts", postService.findAll(pageable));
    model.addAttribute("createPostDto", new CreatePost("", 1L));
    model.addAttribute("fact", dogFactService.getDogFact());
    // TODO: Change this to the actual account once security is in place
    model.addAttribute("account", accountService.findById(1L));

    return "index";
  }

  @GetMapping("/{username}")
  public String user(@PathVariable("username") @NotBlank String username, Model model, RedirectAttributes redirectAttributes, @PageableDefault Pageable pageable) {
    ResponseAccount queryAccount;
    try {
      queryAccount = accountService.findByUsername(username);
    }
    catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "User '"+ username +"' not found");
      return "redirect:/";
    }

    model.addAttribute("accountPosts", postService.findByUsername(username, pageable));
    model.addAttribute("queryAccount", queryAccount);
    model.addAttribute("fact", dogFactService.getDogFact());
    // TODO: Change this to the actual account once security is in place
    model.addAttribute("account", accountService.findById(1L));

    return "profile";
  }

  @GetMapping("/post/load")
  public String loadPosts(@RequestParam("page") int page, Model model, @PageableDefault(size = 5) Pageable pageable) {
    pageable = PageRequest.of(page, pageable.getPageSize());
    model.addAttribute("posts", postService.findAll(pageable));
    return "partials/posts-wrapper";
  }

  @PostMapping("/post/add")
  public String addPost(@ModelAttribute @NotNull CreatePost dto,
                        RedirectAttributes redirectAttributes) {
    if (dto.content() == null || dto.content().trim().isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "Post content cannot be empty");
      redirectAttributes.addFlashAttribute("createPostDto", dto);
      return "redirect:/";
    }

    if (dto.content().length() > 255) {
      redirectAttributes.addFlashAttribute("error", "Post content cannot exceed 255 characters");
      redirectAttributes.addFlashAttribute("createPostDto", dto);
      return "redirect:/";
    }
    postService.addPost(dto);

    redirectAttributes.addFlashAttribute("success", true);
    redirectAttributes.addFlashAttribute("createPostDto", new CreatePost("", dto.accountId()));

    return "redirect:/";
  }

  @GetMapping("/account/{id}/image")
  public ResponseEntity<byte[]> getAccountImage(@PathVariable("id") @Positive @NotNull Long id) throws IOException {
    byte[] accountImage = accountService.getAccountImage(id);

    if (accountImage == null) {
      ClassPathResource resource = new ClassPathResource("static/images/logo/BarkrNoText.png");
      try (InputStream is = resource.getInputStream()) {
        accountImage = is.readAllBytes();
      }
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
            .body(accountImage);
  }

  @PostMapping("/account/{id}/upload")
  public String uploadImage(@PathVariable @Positive @NotNull Long id, @RequestParam("file") @NotNull MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
    if (file.isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "File is empty");
      return "redirect:/";
    }
    if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
      redirectAttributes.addFlashAttribute("error", "File is not an image");
      return "redirect:/";
    }
    if (file.getSize() > 5 * 1024 * 1024) {
      redirectAttributes.addFlashAttribute("error", "File is too large");
      return "redirect:/";
    }
    accountService.updateImage(id, file.getBytes());
    return "redirect:/";
  }
}

