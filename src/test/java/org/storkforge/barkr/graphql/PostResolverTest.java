package org.storkforge.barkr.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.ResponsePost;

import java.time.LocalDateTime;
import java.util.List;

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

        when(postService.findAll()).thenReturn(List.of(post1, post2));

        graphQlTester.document("""
                            query {
                              posts {
                                id
                                content
                              }
                            }
                        """)
                .execute()
                .path("posts").entityList(ResponsePost.class).hasSize(2)
                .path("posts[0].content").entity(String.class).isEqualTo("First post")
                .path("posts[1].content").entity(String.class).isEqualTo("Second post");
    }


}
