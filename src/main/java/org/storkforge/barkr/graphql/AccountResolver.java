package org.storkforge.barkr.graphql;

import graphql.GraphQLException;
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
            throw new GraphQLException("Error retrieving Accounts: " + e.getMessage(), e);
        }
    }

    @QueryMapping("account")
    public ResponseAccount account(@Argument @NotNull @Positive Long id) {
        try {
            return accountService.findById(id);
        } catch (Exception e) {
            throw new GraphQLException("Error retrieving Account: " + e.getMessage(), e);
        }
    }

}
