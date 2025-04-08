package org.storkforge.barkr.domain.services.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Account account;

    @InjectMocks
    private ApiAccountService accountService;


    @Nested
    class NoAccountsTests {

        @Test
        @DisplayName("No accounts in database throws exception")
        void noAccountsInDatabaseThrowsException() {
            when(accountRepository.findAll()).thenReturn(List.of()).thenThrow(AccountNotFound.class);
            assertThatThrownBy(accountService::findAll).isInstanceOf(AccountNotFound.class).hasMessage("No account record(s) found in database");

        }


        @Test
        @DisplayName("Invalid Id or nonexistent throws exception")
        void invalidIdOrNonexistentThrowsException() {
            when(accountRepository.findById(anyLong())).thenThrow(new AccountNotFound("Account with id: 1 was not found"));
            assertThatThrownBy(() -> accountService.findOne(1L)).isInstanceOf(AccountNotFound.class).hasMessage("Account with id: 1 was not found");

        }

        @Test
        @DisplayName("Null account throws exceptions")
        void nullAccountThrowsExceptions() {

            when(accountRepository.findAll()).thenReturn(null).thenThrow(new AccountNotFound("No account record(s) found in database"));
            assertThatThrownBy(accountService::findAll).isInstanceOf(AccountNotFound.class).hasMessage("No account record(s) found in database");

        }
    }

    @Nested
    class AccountsTests {

        @Test
        @DisplayName("findAll returns all users in database")
        void findAllReturnsAllUsersInDatabase() {
            Account mockAccount = mock(Account.class);
            Account mockAccount2 = mock(Account.class);
            when(mockAccount.getId()).thenReturn(1L);
            when(mockAccount2.getId()).thenReturn(2L);
            when(mockAccount.getUsername()).thenReturn("John Doe");
            when(mockAccount2.getUsername()).thenReturn("Jane Doe");
            when(mockAccount.getCreatedAt()).thenReturn(LocalDateTime.now());
            when(mockAccount2.getCreatedAt()).thenReturn(LocalDateTime.now());

            List<Account> mockAccounts = List.of(mockAccount, mockAccount2);
            when(accountRepository.findAll()).thenReturn(mockAccounts);

            var result = accountService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.getFirst()).extracting("username").isEqualTo("John Doe");
            verify(accountRepository, times(1)).findAll();

        }





    @Test
    @DisplayName("findOne returns valid account when valid id is passed")
    void findOneReturnsValidAccountWhenValidIdIsPassed() {

        LocalDateTime constantTime = LocalDateTime.of(2020, 1, 1, 1, 1);

        Account mockAccount = mock(Account.class);
        when(mockAccount.getId()).thenReturn(1L);
        when(mockAccount.getUsername()).thenReturn("John Doe");
        when(mockAccount.getCreatedAt()).thenReturn(constantTime);

        when(accountRepository.findById(eq(1L))).thenReturn(Optional.of(mockAccount));

        var result = accountService.findOne(1L);

        assertThat(result).extracting("username").isEqualTo("John Doe");
        assertThat(result).extracting("id").isEqualTo(1L);
        assertThat(result).extracting("createdAt").isEqualTo(constantTime);
        verify(accountRepository, times(1)).findById(eq(1L));
        }
    }
}
