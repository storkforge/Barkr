package org.storkforge.barkr.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

@RestController
@RequestMapping("/api")
public class ApiAccountController {

    private final AccountService accountService;

    public ApiAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public Page<ResponseAccount> accounts(@PageableDefault Pageable pageable) {
        return accountService.findAll(pageable);
    }

    @GetMapping("/accounts/{id}")
    public ResponseAccount account(@PathVariable Long id) {
        return accountService.findById(id);
    }
}
