package org.storkforge.barkr.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.storkforge.barkr.domain.IssuedApiKeyService;
import org.storkforge.barkr.dto.apiKeyDto.UpdateApiKey;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    IssuedApiKeyService issuedApiKeyService;

    public ApiKeyAuthenticationFilter(IssuedApiKeyService issuedApiKeyService) {
        this.issuedApiKeyService = issuedApiKeyService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader("API-KEY");

        try {
            if (isValidApiKey(apiKey)) {
                Authentication auth = new ApiKeyAuthenticationToken(apiKey);
                SecurityContextHolder.getContext().setAuthentication(auth);

                var hashedApiKey = issuedApiKeyService.hashedApiKey(apiKey);
                var foundApikey = issuedApiKeyService.issuedApiKeyExists(hashedApiKey);
                var updateApikey = new UpdateApiKey(
                        foundApikey.get().getReferenceId(),
                        null,
                        null,
                        LocalDateTime.now());
                issuedApiKeyService.updateApiKey(updateApikey);


            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }


        filterChain.doFilter(request, response);

    }

    private boolean isValidApiKey(String apiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        issuedApiKeyService.revokeExpiredApiKeys();
        if (apiKey == null) {
            return false;
        }
        return issuedApiKeyService.apiKeyValidation(apiKey);

    }



}
