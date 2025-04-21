package org.storkforge.barkr.web.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.DogFactService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.roles.BarkrRole;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.CreatePost;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.exceptions.AccountNotFound;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
  public String index(Model model, @PageableDefault Pageable pageable, @AuthenticationPrincipal OidcUser user) {
    long id = 1L;

    if(user != null) {
      var currentUser = accountService.findByGoogleOidc2Id(user.getName());
      id = currentUser.isEmpty() ? 1L : currentUser.get().getId();
    }


    model.addAttribute("posts", postService.findAll(pageable));
    model.addAttribute("createPostDto", new CreatePost("", id));
    model.addAttribute("fact", dogFactService.getDogFact());
    model.addAttribute("account", accountService.findById(id));

    return "index";
  }

  @GetMapping("/{username}")
  public String user(@PathVariable("username") @NotBlank String username, Model model, RedirectAttributes redirectAttributes, @PageableDefault Pageable pageable, @AuthenticationPrincipal OidcUser user) {
    Long id = 1L;
    if(user != null) {
      var currentUser = accountService.findByGoogleOidc2Id(user.getName());
      id = currentUser.isEmpty() ? 1L : currentUser.get().getId();
      username = currentUser.get().getUsername().isEmpty() ? username : currentUser.get().getUsername();
    }
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
    model.addAttribute("account", accountService.findById(id));

    return "profile";
  }

  @GetMapping("/barkr/logout")
  public String barkrLogout(){
    SecurityContextHolder.clearContext();
    return "fragments/barkr-logout";
  }

  @GetMapping("/post/load")
  public String loadPosts(@RequestParam("page") int page, Model model, @PageableDefault Pageable pageable) {
    pageable = PageRequest.of(page, pageable.getPageSize());
    Page<ResponsePost> postPage = postService.findAll(pageable);

    if (postPage.isEmpty()) return "partials/empty";

    model.addAttribute("posts", postPage);
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


@PostMapping("/unlock-easter-egg")
public ResponseEntity<?> premiumUser(@RequestParam String code, @AuthenticationPrincipal OidcUser user){

    var sanitizedCode = code.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

    if (!sanitizedCode.trim().equals("upupdowndownleftrightleftrightba")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Better try next time: \uD83D\uDE1C");

    }

    var account = accountService.findByGoogleOidc2Id(user.getName()).orElseThrow(() -> new AccountNotFound("No account found"));
    account.setRoles(new HashSet<>(Set.of(BarkrRole.USER, BarkrRole.PREMIUM)));

    Set<GrantedAuthority> mappedAuthorities = account.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getAuthorityValue()))
            .collect(Collectors.toSet());

    OidcUser updateUser = new DefaultOidcUser(mappedAuthorities, user.getIdToken(), user.getUserInfo());
    OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(updateUser, updateUser.getAuthorities(), updateUser.getAccessTokenHash());

    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(auth);


    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Congrats: \uD83D\uDC4D");

  }




}
