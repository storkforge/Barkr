package org.storkforge.barkr.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.storkforge.barkr.api.controller.ApiKeyController;
import org.storkforge.barkr.domain.IssuedApiKeyService;
import org.storkforge.barkr.domain.entity.Account;
import org.storkforge.barkr.dto.apiKeyDto.ResponseApiKeyList;
import org.storkforge.barkr.dto.apiKeyDto.ResponseApiKeyOnce;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ApiKeyController.class)
@AutoConfigureMockMvc
@WithMockUser(roles = "USER")
class ApiKeyControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private IssuedApiKeyService issuedApiKeyService;

    @MockitoBean
    private AccountRepository accountRepository;

    private OAuth2AuthenticationToken mockOAuth2Auth(String name) {
        OidcUser mockUser = Mockito.mock(OidcUser.class);
        Mockito.when(mockUser.getName()).thenReturn(name);

        return new OAuth2AuthenticationToken(
                mockUser,
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                "oidc"
        );
    }

    private static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder postWithCsrf(String url) {
        return post(url).with(csrf());
    }

    @Test
    @DisplayName("GET /apikeys/apikeyform returns form view")
    void showApiKeyForm() throws Exception {
        mvc.perform(get("/apikeys/apikeyform"))
                .andExpect(status().isOk())
                .andExpect(view().name("apikeys"));
    }

    @Test
    @DisplayName("GET /apikeys/result redirects if response is null")
    void showGeneratedApiKey_redirectsWhenResponseIsNull() throws Exception {
        mvc.perform(get("/apikeys/result"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apikeys/apikeyform"));
    }

    @Test
    @DisplayName("POST /apikeys/generate redirects to result")
    void generateApiKey() throws Exception {
        Mockito.when(issuedApiKeyService.generateRawApiKey()).thenReturn("fake-key");
        Mockito.when(issuedApiKeyService.hashedApiKey("fake-key")).thenReturn("hashed-key");
        Mockito.when(issuedApiKeyService.apiKeyExists("hashed-key")).thenReturn(false);

        mvc.perform(postWithCsrf("/apikeys/generate")
                        .param("apiKeyName", "test-key"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apikeys/result"));
    }

    @Test
    void showGeneratedApiKey_rendersViewWithResponse() throws Exception {
        ResponseApiKeyOnce response = new ResponseApiKeyOnce("API-KEY", "key", "store safely");

        mvc.perform(get("/apikeys/result")
                        .flashAttr("response", response))
                .andExpect(status().isOk())
                .andExpect(view().name("apikeys/result"))
                .andExpect(model().attributeExists("response"));
    }

    @Test
    void generateApiKey_retriesUntilUnique() throws Exception {
        Mockito.when(issuedApiKeyService.generateRawApiKey())
                .thenReturn("duplicate", "unique");
        Mockito.when(issuedApiKeyService.hashedApiKey(any()))
                .thenReturn("hashed-duplicate", "hashed-unique");
        Mockito.when(issuedApiKeyService.apiKeyExists("hashed-duplicate")).thenReturn(true);
        Mockito.when(issuedApiKeyService.apiKeyExists("hashed-unique")).thenReturn(false);

        mvc.perform(postWithCsrf("/apikeys/generate")
                        .param("apiKeyName", "retry-test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apikeys/result"));

        verify(issuedApiKeyService, Mockito.times(2)).generateRawApiKey();
    }


    @Test
    @DisplayName("GET /apikeys/mykeys displays user keys")
    void showMyKeys() throws Exception {
        var account = new Account();
        account.setUsername("test-user");

        Mockito.when(accountRepository.findByGoogleOidc2Id(any())).thenReturn(Optional.of(account));
        Mockito.when(issuedApiKeyService.allApiKeys()).thenReturn(new ResponseApiKeyList(List.of()));

        mvc.perform(get("/apikeys/mykeys")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(mockOAuth2Auth("test-oidc-id"))))
                .andExpect(status().isOk())
                .andExpect(view().name("apikeys/mykeys"))
                .andExpect(model().attributeExists("keys"))
                .andExpect(model().attributeExists("account"));
    }
    @Test
    @DisplayName("POST /apikeys/mykeys/revoke revokes a key")
    void revokeKey() throws Exception {
        String fakeReferenceId = UUID.randomUUID().toString();

        mvc.perform(postWithCsrf("/apikeys/mykeys/revoke")
                        .param("referenceId", fakeReferenceId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apikeys/mykeys"));
    }

    @Test
    @DisplayName("POST /apikeys/mykeys/nameupdate updates key name")
    void updateKeyName() throws Exception {
        String fakeReferenceId = UUID.randomUUID().toString();

        mvc.perform(postWithCsrf("/apikeys/mykeys/nameupdate")
                        .param("referenceId", fakeReferenceId)
                        .param("apiKeyName", "updated-name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apikeys/mykeys"));
    }
}