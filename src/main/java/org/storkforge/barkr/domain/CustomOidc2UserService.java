package org.storkforge.barkr.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.roles.BarkrRole;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomOidc2UserService extends OidcUserService {
    private final Logger log = LoggerFactory.getLogger(CustomOidc2UserService.class);

    private final AccountRepository accountRepository;

    public CustomOidc2UserService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);
        String username = user.getAttributes().get("name").toString();
        String oidcId = user.getName();
        Account account;

        var accountFound = accountRepository.findByGoogleOidc2Id(user.getName());

        if (accountFound.isPresent()) {
            account = accountFound.get();

        } else {

            account = new Account();
            GoogleAccountApiKeyLink link = new GoogleAccountApiKeyLink();
            account.setUsername(username);
            account.setBreed("Snoopy");
            account.setGoogleOidc2Id(oidcId);
            account.setImage(null);
            account.setRoles(new HashSet<>(Set.of(BarkrRole.USER)));
            link.setAccount(account);
            log.info("New record added to database");
            account.setGoogleAccountApiKeyLink(link);
            try {

                accountRepository.save(account);
            } catch (Exception e) {
                throw new OAuth2AuthenticationException("Failed to save account " + account);
            }
        }

        Set<GrantedAuthority> mappedAuthorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthorityValue()))
                .collect(Collectors.toSet());

        return new DefaultOidcUser(mappedAuthorities, user.getIdToken(), user.getUserInfo());

    }


}
