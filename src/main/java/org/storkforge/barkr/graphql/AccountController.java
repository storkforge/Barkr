package org.storkforge.barkr.graphql;



import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.domain.entity.Account;

import java.util.List;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @QueryMapping("Accounts")
    public List<Account> accounts() {
        return accountService.findAll();
    }

    @QueryMapping("Account")
    public Account account(@Argument Long id) {
        return accountService.findById(id);
    }

}
