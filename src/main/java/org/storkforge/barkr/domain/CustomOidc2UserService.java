package org.storkforge.barkr.domain;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidc2UserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException{
        OidcUser user = super.loadUser(userRequest);
        System.out.println("###########################################################################################################");
        System.out.println("The OIDC user found:");
        System.out.println(user);
        System.out.println("###########################################################################################################");
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        System.out.println("###########################################################################################################");

        //TODO For Servic
        // - 1. Check that user exists in db with lookup on user.getName() which is a unique value.
        // - 2. If user exists retrieve user object and if not create new user entity.

        return user;

    }

}
