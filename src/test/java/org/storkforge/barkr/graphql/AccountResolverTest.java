package org.storkforge.barkr.graphql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.exceptions.AccountNotFound;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@GraphQlTest({AccountResolver.class})
@ExtendWith(MockitoExtension.class)
class AccountResolverTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private AccountService accountService;

    @Test
    void testGetAccountById() {
        ResponseAccount account = new ResponseAccount(10L, "testAccount", LocalDateTime.now(), "beagle", new byte[0]);

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
                .path("account.username").entity(String.class).isEqualTo("testAccount");
    }

    @Test
    void testGetAllAccounts() {
        ResponseAccount account1 = new ResponseAccount(1L, "accountOne", LocalDateTime.now(), "beagle", new byte[0]);
        ResponseAccount account2 = new ResponseAccount(2L, "accountTwo", LocalDateTime.now(), "beagle", new byte[0]);

        Pageable pageable = PageRequest.of(0, 10);

        when(accountService.findAll(pageable)).thenReturn(new PageImpl<>(List.of(account1, account2)));

        graphQlTester.document("""
                            query {
                              accounts(page: 0, size: 10) {
                                content {
                                    id
                                    username
                                }
                              }
                            }
                        """)
                .execute()
                .path("accounts.content").entityList(ResponseAccount.class).hasSize(2)
                .path("accounts.content[0].username").entity(String.class).isEqualTo("accountOne")
                .path("accounts.content[1].username").entity(String.class).isEqualTo("accountTwo");
    }

    @Test
    @DisplayName("Handles error for nonexistent id")
    void handlesErrorForNonexistentId() {
        when(accountService.findById(1L)).thenThrow(new AccountNotFound("Account with id: 1 was not found"));

        graphQlTester.document("""
                        query {
                            account(id: "1") {
                             id
                             username
                            }
                        }
                        """)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.getFirst().getMessage()).isEqualTo("Account with id: 1 was not found");
                });
    }
}
