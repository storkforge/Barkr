package org.storkforge.barkr.web.controller;

import com.redis.testcontainers.RedisContainer;
import org.htmlunit.WebClient;
import org.htmlunit.html.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.annotation.Transient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.api.controller.ApiKeyController;
import org.storkforge.barkr.domain.IssuedApiKeyService;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.entity.Post;
import org.storkforge.barkr.domain.roles.BarkrRole;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

  @Container
  @ServiceConnection
  static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:latest"));

  @Mock
  private IssuedApiKeyService issuedApiKeyService;

  @InjectMocks
  private ApiKeyController apiKeyController;


  @Autowired
  private AccountRepository accountRepository;

  @Transient
  private GoogleAccountApiKeyLink googleAccountApiKeyLink;

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
    @WithMockUser
    @DisplayName("Can view all posts")
    void viewAllPostsPage() throws IOException {
      Account mockAccount = new Account();
      GoogleAccountApiKeyLink mockKeyLink = new GoogleAccountApiKeyLink();

      mockAccount.setUsername("mockAccount");
      mockAccount.setBreed("husky");
      mockAccount.setGoogleOidc2Id("6");

      mockAccount.setImage(null);
      mockAccount.setRoles(new HashSet<>(Set.of(BarkrRole.USER, BarkrRole.PREMIUM)));

      mockKeyLink.setAccount(mockAccount);
      mockAccount.setGoogleAccountApiKeyLink(mockKeyLink);

      Account mockAccount2 = new Account();
      GoogleAccountApiKeyLink mockKeyLink2 = new GoogleAccountApiKeyLink();

      mockAccount2.setUsername("mockAccount2");
      mockAccount2.setBreed("beagle");

      mockAccount2.setGoogleOidc2Id("7");
      mockAccount2.setImage(null);

      mockAccount2.setRoles(new HashSet<>(Set.of(BarkrRole.USER, BarkrRole.PREMIUM)));
      mockKeyLink2.setAccount(mockAccount2);
      mockAccount2.setGoogleAccountApiKeyLink(mockKeyLink2);

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
    @WithMockUser(authorities = {"ROLE_USER", "ROLE_PREMIUM"})
    @DisplayName("Verify add post form exist")
    void verifyAddPostFormExist() throws IOException {
      HtmlPage page = htmlClient.getPage("/");
      HtmlForm form = page.getFormByName("index-first-form");
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
    @WithMockUser
    @DisplayName("Can submit the add post form")
    void verifyAddPostFormSubmitted() throws IOException {
      HtmlPage page = htmlClient.getPage("/");

      HtmlForm form = page.getFormByName("index-first-form");
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
    @WithMockUser
    @DisplayName("Redirects the user on empty form value")
    void redirectOnEmptyForm() throws IOException {
      HtmlPage page = htmlClient.getPage("/");


      HtmlForm form = page.getFormByName("index-first-form");
      HtmlButton submitButton = (HtmlButton) form.getElementsByTagName("button").getFirst();

      HtmlPage resultPage = submitButton.click();

      assertThat(((HtmlDivision) resultPage.getFirstByXPath("//div[@class='error-message']")).getTextContent()).contains("Post content cannot be empty");
    }

    @Test
    @WithMockUser
    @DisplayName("Redirects the user if value is greater than 255 characters")
    void redirectOnTooManyCharactersInput() throws IOException {
      HtmlPage page = htmlClient.getPage("/");

      HtmlForm form = page.getFormByName("index-first-form");
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
    @WithMockUser
    @DisplayName("Can view profile page")
    void viewProfilePage() throws IOException {
      Account mockAccount = new Account();
      GoogleAccountApiKeyLink mockKeyLink = new GoogleAccountApiKeyLink();
      mockAccount.setUsername("mockAccount");
      mockAccount.setBreed("husky");
      mockAccount.setGoogleOidc2Id("4");
      mockAccount.setImage(null);
      mockAccount.setRoles(new HashSet<>(Set.of(BarkrRole.USER, BarkrRole.PREMIUM)));
      mockKeyLink.setAccount(mockAccount);
      mockAccount.setGoogleAccountApiKeyLink(mockKeyLink);

      Account mockAccount2 = new Account();
      GoogleAccountApiKeyLink mockKeyLink2 = new GoogleAccountApiKeyLink();
      mockAccount2.setUsername("mockAccount2");
      mockAccount2.setBreed("beagle");
      mockAccount2.setGoogleOidc2Id("5");
      mockAccount2.setImage(null);
      mockAccount2.setRoles(new HashSet<>(Set.of(BarkrRole.USER, BarkrRole.PREMIUM)));
      mockKeyLink2.setAccount(mockAccount2);
      mockAccount2.setGoogleAccountApiKeyLink(mockKeyLink2);

      accountRepository.saveAll(List.of(mockAccount, mockAccount2));

      Post mockPost = new Post();
      mockPost.setAccount(mockAccount);
      mockPost.setContent("mockPost");

      Post mockPost2 = new Post();
      mockPost2.setAccount(mockAccount2);
      mockPost2.setContent("mockPost2");

      postRepository.saveAll(List.of(mockPost, mockPost2));
      HtmlPage page = htmlClient.getPage("/mockAccount");

      mockAccount2.setRoles(Set.of());
      String pageContent = page.asNormalizedText();

      assertAll(
              () -> assertThat(page.getTitleText()).isEqualTo("mockAccount / Barkr"),
              () -> assertThat(pageContent).contains("mockPost"),
              () -> assertThat(pageContent).doesNotContain("mockPost2")
      );
    }

    @Test
    @WithMockUser
    @DisplayName("Redirects the user on nonexistent account")
    void redirectsOnNonexistentAccount() throws IOException {
      HtmlPage page = htmlClient.getPage("/nonExistent");

      assertThat(((HtmlDivision) page.getFirstByXPath("//div[@class='error-message']")).getTextContent()).contains("User 'nonExistent' not found");
    }
  }
}
