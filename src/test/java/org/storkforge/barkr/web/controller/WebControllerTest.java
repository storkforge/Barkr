package org.storkforge.barkr.web.controller;

import org.htmlunit.WebClient;
import org.htmlunit.html.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ActiveProfiles({"default", "dev"})
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

  @Autowired
  private CacheManager cacheManager;

  @BeforeEach
  void clearCache() {
    cacheManager.getCacheNames().forEach(name ->
            Optional.ofNullable(cacheManager.getCache(name)).ifPresent(Cache::clear)
    );
  }

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
              () -> assertThat(((HtmlDivision) resultPage.getFirstByXPath("//div[@class='success-message']")).getTextContent()).contains("Successfully barked!"),
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
    void redirectOnTooManyCharactersInput() throws IOException {
      HtmlPage page = htmlClient.getPage("/");

      HtmlForm form = page.getForms().getFirst();
      HtmlTextArea contentInput = form.getTextAreaByName("content");
      HtmlButton submitButton = (HtmlButton) form.getElementsByTagName("button").getFirst();

      String tooLongContent = "A".repeat(256);
      contentInput.setText(tooLongContent);

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
    void redirectsOnNonexistentAccount() throws IOException {
      HtmlPage page = htmlClient.getPage("/nonExistent");

      assertThat(((HtmlDivision) page.getFirstByXPath("//div[@class='error-message']")).getTextContent()).contains("User 'nonExistent' not found");
    }
  }
}
