package org.storkforge.barkr.graphql;



import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

import java.util.List;

@Controller
public class AccountResolver {

    private final AccountService accountService;

    public AccountResolver(AccountService accountService) {
        this.accountService = accountService;
    }

    @QueryMapping("Accounts")
    public List<ResponseAccount> accounts() {
        return accountService.findAll();
    }

    @QueryMapping("Account")
    public ResponseAccount account(@Argument Long id) {
        return accountService.findById(id);
    }

}
