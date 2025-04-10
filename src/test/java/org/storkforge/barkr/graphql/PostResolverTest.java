package org.storkforge.barkr.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.storkforge.barkr.web.domain.AccountService;
import org.storkforge.barkr.web.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.web.graphql.AccountResolver;
import org.storkforge.barkr.web.graphql.PostResolver;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@GraphQlTest({PostResolver.class, AccountResolver.class}) // Include all relevant resolvers
class PostResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private PostService postService;

    @Autowired
    private AccountService accountService;

    @Test
    void testGetPostById() {
        ResponseAccount account = new ResponseAccount(10L, "testUser", LocalDateTime.now(), "beagle", new byte[0]);
        ResponsePost post = new ResponsePost(1L, "Test content", account, LocalDateTime.now());

        when(postService.findOne(1L)).thenReturn(post);
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
                .path("post.account.username").entity(String.class).isEqualTo("testUser");
    }

    @Test
    void testGetAllPosts() {
        ResponseAccount account = new ResponseAccount(10L, "testUser", LocalDateTime.now(), "beagle", new byte[0]);
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


    @TestConfiguration
    static class MockConfig {
        @Bean
        public PostService postService() {
            return mock(PostService.class);
        }

        @Bean
        public AccountService accountService() {
            return mock(AccountService.class);
        }

        @Bean
        public PostResolver postResolver(PostService postService) {
            return new PostResolver(postService);
        }

    }
}
