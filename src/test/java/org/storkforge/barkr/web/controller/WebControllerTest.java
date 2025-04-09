package org.storkforge.barkr.web.controller;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Test
    @DisplayName("Can view all posts")
    void viewAllPostsPage() throws IOException {
        Account mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setUsername("mockAccount");
        mockAccount.setBreed("husky");

        Account mockAccount2 = new Account();
        mockAccount2.setId(2L);
        mockAccount2.setUsername("mockAccount2");
        mockAccount2.setBreed("beagle");

        accountRepository.saveAll(List.of(mockAccount, mockAccount2));

        Post mockPost = new Post();
        mockPost.setId(1L);
        mockPost.setAccount(mockAccount);
        mockPost.setContent("mockPost");

        Post mockPost2 = new Post();
        mockPost2.setId(2L);
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
}