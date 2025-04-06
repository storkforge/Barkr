package org.storkforge.barkr.domain;

import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;
import org.storkforge.barkr.mapper.AccountMapper;

import java.util.List;

@Service
@Transactional
public class AccountService {
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<ResponseAccount> findAll() {
        log.info("Finding all accounts");
        return accountRepository
                .findAll()
                .stream()
                .map(AccountMapper::mapToResponse)
                .toList();
    }

    public ResponseAccount findByUsername(String username) {
        log.info("Finding account by username {}", username);
        return accountRepository
                .findByUsernameEqualsIgnoreCase(username)
                .map(AccountMapper::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Account with username " + username + " not found"));
    }

    public ResponseAccount findById(Long id) {
        log.info("Finding account by id {}", id);
        return accountRepository
                .findById(id)
                .map(AccountMapper::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));
    }

    public byte[] getAccountImage(Long id) {
        log.info("Getting account image {}", id);

        return accountRepository.getAccountImage(id);
    }

    public void updateImage(Long id, byte[] image) {
        log.info("Updating image for user with id {}", id);

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Account with id " + id + " not found"));

        account.setImage(image);

        accountRepository.save(account);
    }
}
