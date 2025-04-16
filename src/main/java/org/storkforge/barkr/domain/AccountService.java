package org.storkforge.barkr.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.mapper.AccountMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Cacheable(value = "allAccounts")
    public List<ResponseAccount> findAll() {
        log.info("Finding all accounts");
        return accountRepository
                .findAll()
                .stream()
                .map(AccountMapper::mapToResponse)
                .toList();
    }

    @Cacheable("accountById")
    public ResponseAccount findById(Long id) {
        log.info("Finding account by id {}", id);
        return accountRepository
                .findById(id)
                .map(AccountMapper::mapToResponse)
                .orElseThrow(() -> new AccountNotFound("Account with id: " + id + " not found"));
    }

    @Cacheable(value = "accountByUsername")
    public ResponseAccount findByUsername(String username) {
        log.info("Finding account by username {}", username);
        return accountRepository
                .findByUsernameEqualsIgnoreCase(username)
                .map(AccountMapper::mapToResponse)
                .orElseThrow(() -> new AccountNotFound("Account with username: " + username + " not found"));
    }

    @Cacheable("accountImage")
    public byte[] getAccountImage(Long id) throws IOException {
        log.info("Getting account image {}", id);

        Optional<byte[]> accountImage = accountRepository.getAccountImage(id);

        if (accountImage.isPresent()) {
            return accountImage.get();
        }

        log.info("Account image not found. Falling back to default image");
        ClassPathResource resource = new ClassPathResource("static/images/logo/BarkrNoText.png");
        try (InputStream is = resource.getInputStream()) {
            return is.readAllBytes();
        }
    }

    @CacheEvict(value = "accountImage", key = "#id")
    public void updateImage(Long id, byte[] image) {
        log.info("Updating image for user with id {}", id);

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFound("Account with id: " + id + " was not found"));

        account.setImage(image);

        accountRepository.save(account);
    }
}
