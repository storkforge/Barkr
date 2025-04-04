package org.storkforge.barkr.domain;

import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.accountDto.ResponseAccountList;
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
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(AccountMapper::mapToResponse).toList();
    }

    public ResponseAccount findOne(Long id) {
        log.info("Finding account by id: {}", id);
        return AccountMapper.mapToResponse(accountRepository.findById(id).orElse(null));
    }

}
