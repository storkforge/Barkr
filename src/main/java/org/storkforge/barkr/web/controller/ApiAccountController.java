package org.storkforge.barkr.web.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.accountDto.ResponseAccountList;
import org.storkforge.barkr.mapper.AccountMapper;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiAccountController {

    private final AccountService accountService;

    public ApiAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseAccountList accounts(Model model) {
        return new ResponseAccountList(accountService.findAll());
    }

    @GetMapping("/accounts/{id}")
    public ResponseAccount account(@PathVariable Long id) {
        return accountService.findOne(id);
    }
}
