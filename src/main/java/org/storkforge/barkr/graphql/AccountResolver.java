package org.storkforge.barkr.graphql;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @QueryMapping("accounts")
    public List<ResponseAccount> accounts() {
        try {
            return accountService.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Accounts: " + e.getMessage(), e);
        }
    }

    @QueryMapping("account")
    public ResponseAccount account(@Argument @NotNull @Positive Long id) {
        try {
            ResponseAccount account = accountService.findById(id);
            if (account == null) {
                throw new RuntimeException("Account not found for ID: " + id);
            }
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving Account: " + e.getMessage(), e);
        }
    }

}
