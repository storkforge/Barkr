package org.storkforge.barkr.graphql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.exceptions.PostNotFound;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@GraphQlTest({PostResolver.class, AccountResolver.class})
class PostResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private AccountService accountService;

    @Test
    void testGetPostById() {
        ResponseAccount account = new ResponseAccount(10L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);
        ResponsePost post = new ResponsePost(1L, "Test content", account, LocalDateTime.now());

        when(postService.findById(1L)).thenReturn(post);
        when(accountService.findById(10L)).thenReturn(account);

        graphQlTester.document("""
                            query {
                              post(id: 1) {
                                id
                                content
                                account {
                                  id
                                  username
                                }
                              }
                            }
                        """)
                .execute()
                .path("post.id").entity(Long.class).isEqualTo(1L)
                .path("post.content").entity(String.class).isEqualTo("Test content")
                .path("post.account.username").entity(String.class).isEqualTo("testAccount");
    }

    @Test
    void testGetAllPosts() {
        ResponseAccount account = new ResponseAccount(10L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);
        ResponsePost post1 = new ResponsePost(1L, "First post", account, LocalDateTime.now());
        ResponsePost post2 = new ResponsePost(2L, "Second post", account, LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);

        when(postService.findAll(pageable)).thenReturn(new PageImpl<>(List.of(post1, post2)));

        graphQlTester.document("""
                            query {
                              posts(page: 0, size: 10) {
                                content {
                                    content
                                }
                              }
                            }
                        """)
                .execute()
                .path("posts.content").entityList(ResponsePost.class).hasSize(2)
                .path("posts.content[0].content").entity(String.class).isEqualTo("First post")
                .path("posts.content[1].content").entity(String.class).isEqualTo("Second post");
    }

    @Test
    @DisplayName("Handles error for nonexistent id")
    void handlesErrorForNonexistentId() {
        when(postService.findById(1L)).thenThrow(new PostNotFound("Post with id: 1 was not found"));

        graphQlTester.document("""
                            query {
                              post(id: 1) {
                                id
                                content
                                account {
                                  id
                                  username
                                }
                              }
                            }
                        """)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.getFirst().getMessage()).isEqualTo("Post with id: 1 was not found");
                });
    }
}
