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


@GraphQlTest({AccountResolver.class})
class AccountResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private AccountService accountService;

    @Test
    void testGetAccountById() {
        ResponseAccount account = new ResponseAccount(10L, "testUser", LocalDateTime.now());

        when(accountService.findById(10L)).thenReturn(account);

        graphQlTester.document("""
                        query {
                            Account(id: "10") {
                             id
                             username
                            }
                        }
                        """)
                .execute()
                .path("Account.id").entity(Long.class).isEqualTo(10L)
                .path("Account.username").entity(String.class).isEqualTo("testUser");
    }

    @Test
    void testGetAllAccounts() {
        ResponseAccount account1 = new ResponseAccount(1L, "userOne", LocalDateTime.now());
        ResponseAccount account2 = new ResponseAccount(2L, "userTwo", LocalDateTime.now());

        when(accountService.findAll()).thenReturn(List.of(account1, account2));

        graphQlTester.document("""
                            query {
                              Accounts {
                                id
                                username
                              }
                            }
                        """)
                .execute()
                .path("Accounts").entityList(ResponsePost.class).hasSize(2)
                .path("Accounts[0].username").entity(String.class).isEqualTo("userOne")
                .path("Accounts[1].username").entity(String.class).isEqualTo("userTwo");
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
