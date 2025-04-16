package org.storkforge.barkr.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Nested
    class NoAccountsTests {
        @Test
        @DisplayName("No accounts in database returns empty array")
        void noAccountsInDatabaseReturnsEmptyArray() {
            Pageable pageable = PageRequest.of(0, 10);
            when(accountRepository.findAll(pageable)).thenReturn(Page.empty());

            Page<ResponseAccount> accounts = accountService.findAll(pageable);

            assertThat(accounts).isEqualTo(Page.empty());
        }

        @Test
        @DisplayName("Invalid Id or nonexistent throws exception")
        void invalidIdOrNonexistentThrowsException() {
            when(accountRepository.findById(1L)).thenThrow(new AccountNotFound("Account with id: 1 was not found"));

            assertThatThrownBy(() -> accountService.findById(1L)).isInstanceOf(AccountNotFound.class).hasMessage("Account with id: 1 was not found");
        }

        @Test
        @DisplayName("Invalid username or nonexistent throws exception")
        void invalidUsernameOrNonexistentThrowsException() {
            when(accountRepository.findByUsernameEqualsIgnoreCase("mockAccount")).thenThrow(new AccountNotFound("Account with username: mockAccount was not found"));

            assertThatThrownBy(() -> accountService.findByUsername("mockAccount"))
                    .isInstanceOf(AccountNotFound.class)
                    .hasMessage("Account with username: mockAccount was not found");
        }
    }

    @Nested
    class AccountsTests {
        @Test
        @DisplayName("findAll returns all accounts in database")
        void findAllReturnsAllAccountsInDatabase() {
            LocalDateTime constantTime = LocalDateTime.of(2020, 1, 1, 1, 1);

            Account mockAccount = new Account();
            mockAccount.setId(1L);
            mockAccount.setUsername("John Doe");
            mockAccount.setCreatedAt(constantTime);

            Account MockAccount2 = new Account();
            MockAccount2.setId(2L);
            MockAccount2.setUsername("Jane Doe");
            MockAccount2.setCreatedAt(constantTime);

            List<Account> mockAccounts = List.of(mockAccount, MockAccount2);
            Page<Account> accountPage = new PageImpl<>(mockAccounts);

            Pageable pageable = PageRequest.of(0, 10);
            when(accountRepository.findAll(pageable)).thenReturn(accountPage);

            Page<ResponseAccount> result = accountService.findAll(pageable);

            assertAll(
                    () -> assertThat(result.getContent()).hasSize(2),
                    () -> assertThat(result.getContent())
                            .extracting("id", "username", "createdAt")
                            .containsExactlyInAnyOrder(
                                    tuple(1L, "John Doe", constantTime),
                                    tuple(2L, "Jane Doe", constantTime)
                            )
            );
        }

        @Test
        @DisplayName("findById returns valid account when valid id is passed")
        void findByIdReturnsValidAccountWhenValidIdIsPassed() {
            LocalDateTime constantTime = LocalDateTime.of(2020, 1, 1, 1, 1);

            Account mockAccount = new Account();
            mockAccount.setId(1L);
            mockAccount.setUsername("John Doe");
            mockAccount.setCreatedAt(constantTime);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

            ResponseAccount result = accountService.findById(1L);

            assertAll(
                    () -> assertThat(result).extracting("username").isEqualTo(mockAccount.getUsername()),
                    () -> assertThat(result).extracting("id").isEqualTo(mockAccount.getId()),
                    () -> assertThat(result).extracting("createdAt").isEqualTo(mockAccount.getCreatedAt())
            );
        }

        @Test
        @DisplayName("findByUsername returns valid account when valid username is passed")
        void findByUsernameReturnsValidAccountWhenValidUsernameIsPassed() {
            LocalDateTime constantTime = LocalDateTime.of(2020, 1, 1, 1, 1);

            Account mockAccount = new Account();
            mockAccount.setId(1L);
            mockAccount.setUsername("John Doe");
            mockAccount.setCreatedAt(constantTime);

            when(accountRepository.findByUsernameEqualsIgnoreCase("John Doe")).thenReturn(Optional.of(mockAccount));

            ResponseAccount result = accountService.findByUsername("John Doe");

            assertAll(
                    () -> assertThat(result).extracting("username").isEqualTo(mockAccount.getUsername()),
                    () -> assertThat(result).extracting("id").isEqualTo(mockAccount.getId()),
                    () -> assertThat(result).extracting("createdAt").isEqualTo(mockAccount.getCreatedAt())
            );
        }
    }

    @Nested
    class AccountImageTests {
        @Test
        @DisplayName("Can get image from account")
        void canGetImageFromAccount() throws IOException {
            byte[] imageBytes = "fake-image-bytes".getBytes();

            when(accountRepository.getAccountImage(1L)).thenReturn(Optional.of(imageBytes));

            byte[] result = accountService.getAccountImage(1L);

            assertThat(result).isEqualTo(imageBytes);
        }

        @Test
        @DisplayName("Defaults to logo if not found")
        void defaultsToLogoIfNotFound() throws IOException {
            when(accountRepository.getAccountImage(1L)).thenReturn(Optional.empty());

            byte[] defaultImage;
            ClassPathResource resource = new ClassPathResource("static/images/logo/BarkrNoText.png");
            try (InputStream is = resource.getInputStream()) {
                defaultImage = is.readAllBytes();
            }

            byte[] result = accountService.getAccountImage(1L);

            assertThat(result).isEqualTo(defaultImage);
        }

        @Test
        @DisplayName("Can update the image of a account")
        void canUpdateImageOfAccount() {
            when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));

            accountService.updateImage(1L, new byte[0]);

            verify(accountRepository, times(1)).save(any(Account.class));
        }

        @Test
        @DisplayName("updateImage throws error for nonexistent account")
        void updateImageThrowsErrorForNonexistentAccount() {
            when(accountRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.updateImage(1L, new byte[0]))
                    .isInstanceOf(AccountNotFound.class)
                    .hasMessage("Account with id: 1 was not found");
        }
    }

}
