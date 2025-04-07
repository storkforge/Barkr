package org.storkforge.barkr.graphql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.PostService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.postDto.ResponsePost;

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
        ResponsePost post = new ResponsePost(1L, "Test content", 10L, LocalDateTime.now());
        ResponseAccount account = new ResponseAccount(10L, "testUser", LocalDateTime.now());

        when(postService.findById(1L)).thenReturn(post);
        when(accountService.findById(10L)).thenReturn(account);

        graphQlTester.document("""
                            query {
                              Post(id: 1) {
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
                .path("Post.id").entity(Long.class).isEqualTo(1L)
                .path("Post.content").entity(String.class).isEqualTo("Test content")
                .path("Post.account.username").entity(String.class).isEqualTo("testUser");
    }

    @Test
    void testGetAllPosts() {
        ResponsePost post1 = new ResponsePost(1L, "First post", 10L, LocalDateTime.now());
        ResponsePost post2 = new ResponsePost(2L, "Second post", 20L, LocalDateTime.now());

        when(postService.findAll()).thenReturn(List.of(post1, post2));

        graphQlTester.document("""
        query {
          Posts {
            id
            content
          }
        }
    """)
                .execute()
                .path("Posts").entityList(ResponsePost.class).hasSize(2)
                .path("Posts[0].content").entity(String.class).isEqualTo("First post")
                .path("Posts[1].content").entity(String.class).isEqualTo("Second post");
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

        @Bean
        public PostFieldResolver postFieldResolver(AccountService accountService) {
            return new PostFieldResolver(accountService);
        }
    }
}
