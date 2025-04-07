package org.storkforge.barkr.graphql;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.storkforge.barkr.domain.AccountService;
import org.storkforge.barkr.dto.postDto.ResponsePost;
import org.storkforge.barkr.dto.accountDto.ResponseAccount;

@Controller
public class PostFieldResolver {

    private final AccountService accountService;

    public PostFieldResolver(AccountService accountService) {
        this.accountService = accountService;
    }

    @SchemaMapping(typeName = "Post", field = "account")
    public ResponseAccount getAccount(ResponsePost post) {
        return accountService.findById(post.accountId());
    }
}

