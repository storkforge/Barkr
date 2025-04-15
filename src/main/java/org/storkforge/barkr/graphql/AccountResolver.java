package org.storkforge.barkr.graphql;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

@Controller
public class AccountResolver {

    private final AccountService accountService;

    public AccountResolver(AccountService accountService) {
        this.accountService = accountService;
    }

    @QueryMapping("accounts")
    public Page<ResponseAccount> accounts(@Argument("page") @PositiveOrZero int page, @Argument("size") @Positive int size) {
        Pageable pageable = PageRequest.of(page, size);

        return accountService.findAll(pageable);
    }

    @QueryMapping("account")
    public ResponseAccount account(@Argument("id") @NotNull @Positive Long id) {
        return accountService.findById(id);
    }
}
