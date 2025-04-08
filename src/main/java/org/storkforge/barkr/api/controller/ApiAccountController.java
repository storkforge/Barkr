package org.storkforge.barkr.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storkforge.barkr.domain.services.api.ApiAccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;
import org.storkforge.barkr.dto.accountDto.ResponseAccountList;

@RestController
@RequestMapping("/api")
public class ApiAccountController {

    private final ApiAccountService accountService;

    public ApiAccountController(ApiAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public ResponseAccountList accounts() {
        return new ResponseAccountList(accountService.findAll());
    }

    @GetMapping("/accounts/{id}")
    public ResponseAccount account(@PathVariable Long id) {
        return accountService.findOne(id);
    }
}
