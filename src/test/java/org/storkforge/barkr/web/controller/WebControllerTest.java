package org.storkforge.barkr.web.controller;

import org.htmlunit.WebClient;
import org.htmlunit.html.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.DogFactService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class WebControllerTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private WebClient htmlClient;

  @Nested
  class IndexRouteTest {
    @Test
    @DisplayName("Can view all posts")
    void viewAllPostsPage() throws IOException {
      Account mockAccount = new Account();
      mockAccount.setUsername("mockAccount");
      mockAccount.setBreed("husky");

      Account mockAccount2 = new Account();
      mockAccount2.setUsername("mockAccount2");
      mockAccount2.setBreed("beagle");

      accountRepository.saveAll(List.of(mockAccount, mockAccount2));

      Post mockPost = new Post();
      mockPost.setAccount(mockAccount);
      mockPost.setContent("mockPost");

      Post mockPost2 = new Post();
      mockPost2.setAccount(mockAccount2);
      mockPost2.setContent("mockPost2");

      postRepository.saveAll(List.of(mockPost, mockPost2));
      HtmlPage page = htmlClient.getPage("/");
      String pageContent = page.asNormalizedText();

      assertAll(
              () -> assertThat(page.getTitleText()).isEqualTo("Home / Barkr"),
              () -> assertThat(pageContent).contains("mockPost"),
              () -> assertThat(pageContent).contains("mockPost2")
      );
    }

    @Test
    @DisplayName("Verify add post form exist")
    void verifyAddPostFormExist() throws IOException {
      HtmlPage page = htmlClient.getPage("/");
      HtmlForm form = page.getForms().getFirst();
      HtmlTextArea contentInput = form.getTextAreaByName("content");

      assertAll(
              () -> assertThat(page.getTitleText()).isEqualTo("Home / Barkr"),
              () -> assertThat(form.getActionAttribute()).contains("/post/add"),
              () -> assertThat(form.getMethodAttribute()).isEqualTo("post"),
              () -> assertThat(contentInput).isNotNull(),
              () -> assertThat(contentInput.hasAttribute("required")).isTrue()
      );
    }

    @Test
    @DisplayName("Can submit the add post form")
    void verifyAddPostFormSubmitted() throws IOException {
      HtmlPage page = htmlClient.getPage("/");

      HtmlForm form = page.getForms().getFirst();
      HtmlTextArea contentInput = form.getTextAreaByName("content");
      HtmlButton submitButton = (HtmlButton) form.getElementsByTagName("button").getFirst();

      contentInput.setText("mockPost");

      HtmlPage resultPage = submitButton.click();
      List<Post> posts = postRepository.findAll();

      assertAll(
              () -> assertThat(((HtmlDivision) resultPage.getFirstByXPath("//div[@class='success-message']")).getTextContent()).contains("Post added successfully!"),
              () -> assertThat(posts).anyMatch(entity -> "mockPost".equals(entity.getContent()))
      );
    }

    @Test
    @DisplayName("Redirects the user on empty form value")
    void redirectOnEmptyForm() throws IOException {
      HtmlPage page = htmlClient.getPage("/");

      HtmlForm form = page.getForms().getFirst();
      HtmlButton submitButton = (HtmlButton) form.getElementsByTagName("button").getFirst();

      HtmlPage resultPage = submitButton.click();

      assertThat(((HtmlDivision) resultPage.getFirstByXPath("//div[@class='error-message']")).getTextContent()).contains("Post content cannot be empty");
    }

    @Test
    @DisplayName("Redirects the user if value is greater than 255 characters")
    void redirectOnToManyCharactersInput() throws IOException {
      HtmlPage page = htmlClient.getPage("/");

      HtmlForm form = page.getForms().getFirst();
      HtmlTextArea contentInput = form.getTextAreaByName("content");
      HtmlButton submitButton = (HtmlButton) form.getElementsByTagName("button").getFirst();

      contentInput.setText("THIS STRING IS 256 CHARACTERS xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

      HtmlPage resultPage = submitButton.click();

      assertThat(((HtmlDivision) resultPage.getFirstByXPath("//div[@class='error-message']")).getTextContent()).contains("Post content cannot exceed 255 characters");
    }
  }

  @Nested
  class ProfileRouteTest {
    @Test
    @DisplayName("Can view profile page")
    void viewProfilePage() throws IOException {
      Account mockAccount = new Account();
      mockAccount.setUsername("mockAccount");
      mockAccount.setBreed("husky");

      Account mockAccount2 = new Account();
      mockAccount2.setUsername("mockAccount2");
      mockAccount2.setBreed("beagle");

      accountRepository.saveAll(List.of(mockAccount, mockAccount2));

      Post mockPost = new Post();
      mockPost.setAccount(mockAccount);
      mockPost.setContent("mockPost");

      Post mockPost2 = new Post();
      mockPost2.setAccount(mockAccount2);
      mockPost2.setContent("mockPost2");

      postRepository.saveAll(List.of(mockPost, mockPost2));
      HtmlPage page = htmlClient.getPage("/mockAccount");
      String pageContent = page.asNormalizedText();

      assertAll(
              () -> assertThat(page.getTitleText()).isEqualTo("mockAccount / Barkr"),
              () -> assertThat(pageContent).contains("mockPost"),
              () -> assertThat(pageContent).doesNotContain("mockPost2")
      );
    }

    @Test
    @DisplayName("Redirects the user on nonexistent account")
    void redirectsOnNonexsistentAccount() throws IOException {
      HtmlPage page = htmlClient.getPage("/mockAccount");

      assertThat(((HtmlDivision) page.getFirstByXPath("//div[@class='error-message']")).getTextContent()).contains("User 'mockAccount' not found");
    }
  }
}

@WebMvcTest(WebController.class)
class WebControllerImageRouteTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AccountService accountService;

  @MockitoBean
  private PostService postService;

  @MockitoBean
  private DogFactService dogFactService;

  @Test
  @DisplayName("Returns account image if present")
  void returnsAccountImageIfPresent() throws Exception {
    byte[] image = "test-image".getBytes();
    when(accountService.getAccountImage(1L)).thenReturn(image);

    mockMvc.perform(get("/account/1/image"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
            .andExpect(content().bytes(image));
  }

  @Test
  @DisplayName("Returns fallback image if account image is null")
  void returnsFallbackImageIfNull() throws Exception {
    byte[] fallback;
    try (InputStream is = new ClassPathResource("static/images/logo/BarkrNoText.png").getInputStream()) {
      fallback = is.readAllBytes();
    }

    when(accountService.getAccountImage(1L)).thenReturn(null);

    mockMvc.perform(get("/account/1/image"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
            .andExpect(content().bytes(fallback));
  }

  @Test
  @DisplayName("Redirects with error if file is empty")
  void fileIsEmpty() throws Exception {
    MockMultipartFile emptyFile = new MockMultipartFile("file", "image.png", "image/png", new byte[0]);

    mockMvc.perform(multipart("/account/1/upload").file(emptyFile))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attribute("error", "File is empty"));
  }

  @Test
  @DisplayName("Redirects with error if file is not an image")
  void fileIsNotImage() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "some-text".getBytes());

    mockMvc.perform(multipart("/account/1/upload").file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attribute("error", "File is not an image"));
  }

  @Test
  @DisplayName("Redirects with error if file is too large")
  void fileIsTooLarge() throws Exception {
    byte[] large = new byte[6 * 1024 * 1024]; // 6MB
    MockMultipartFile file = new MockMultipartFile("file", "big.png", "image/png", large);

    mockMvc.perform(multipart("/account/1/upload").file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attribute("error", "File is too large"));
  }

  @Test
  @DisplayName("Redirects to root after successful image upload")
  void uploadSuccess() throws Exception {
    byte[] img = "image-content".getBytes();
    MockMultipartFile file = new MockMultipartFile("file", "pic.png", "image/png", img);

    doNothing().when(accountService).updateImage(1L, img);

    mockMvc.perform(multipart("/account/1/upload").file(file))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
  }
}