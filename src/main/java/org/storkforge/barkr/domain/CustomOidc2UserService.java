package org.storkforge.barkr.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.roles.BarkrRole;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class CustomOidc2UserService extends OidcUserService {
    private final Logger log = LoggerFactory.getLogger(CustomOidc2UserService.class);

    private final AccountRepository accountRepository;

    public CustomOidc2UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        OidcUser user = super.loadUser(userRequest);
        String username = user.getAttributes().get("name").toString();
        String oidcId = user.getName();

        var accountFound = accountRepository.findByUsernameEqualsIgnoreCase(username);

        if (accountFound.isPresent()) {
            return user;
        }

        Account account = new Account();
        GoogleAccountApiKeyLink link = new GoogleAccountApiKeyLink();
        account.setUsername(username);
        account.setBreed("Snoopy");
        account.setGoogleOidc2Id(oidcId);
        account.setImage(null);
        account.setRoles(new HashSet<>(Set.of(BarkrRole.USER)));
        link.setAccount(account);
        log.info("New record added to database");
        account.setGoogleAccountApiKeyLink(link);


        accountRepository.save(account);


        return user;

    }


}
