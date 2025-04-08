package org.storkforge.barkr.graphql;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.storkforge.barkr.web.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.web.graphql.AccountResolver;

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
        ResponseAccount account = new ResponseAccount(10L, "testUser", LocalDateTime.now(), "beagle", new byte[0]);

        when(accountService.findById(10L)).thenReturn(account);

        graphQlTester.document("""
                        query {
                            account(id: "10") {
                             id
                             username
                            }
                        }
                        """)
                .execute()
                .path("account.id").entity(Long.class).isEqualTo(10L)
                .path("account.username").entity(String.class).isEqualTo("testUser");
    }

    @Test
    void testGetAllAccounts() {
        ResponseAccount account1 = new ResponseAccount(1L, "userOne", LocalDateTime.now(),"beagle", new byte[0]);
        ResponseAccount account2 = new ResponseAccount(2L, "userTwo", LocalDateTime.now(), "beagle", new byte[0]);

        when(accountService.findAll()).thenReturn(List.of(account1, account2));

        graphQlTester.document("""
                            query {
                              accounts {
                                id
                                username
                              }
                            }
                        """)
                .execute()
                .path("accounts").entityList(ResponseAccount.class).hasSize(2)
                .path("accounts[0].username").entity(String.class).isEqualTo("userOne")
                .path("accounts[1].username").entity(String.class).isEqualTo("userTwo");
    }


    @TestConfiguration
    static class MockConfig {

        @Bean
        public AccountService accountService() {
            return mock(AccountService.class);
        }

        @Bean
        public AccountResolver accountResolver(AccountService accountService) {
            return new AccountResolver(accountService);
        }

    }
}
