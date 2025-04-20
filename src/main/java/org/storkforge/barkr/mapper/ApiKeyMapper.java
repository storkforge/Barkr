package org.storkforge.barkr.mapper;

import org.storkforge.barkr.domain.entity.IssuedApiKey;
import org.storkforge.barkr.dto.apiKeyDto.CreateApiKey;
import org.storkforge.barkr.dto.apiKeyDto.GenerateApiKeyRequest;
import org.storkforge.barkr.dto.apiKeyDto.ResponseApiKey;

import java.time.LocalDateTime;

public class ApiKeyMapper {

    private ApiKeyMapper() {
    }

    public static IssuedApiKey mapToEntity(CreateApiKey createApiKey) {
        IssuedApiKey issuedApiKey = new IssuedApiKey();

        if (createApiKey == null) {
            return null;
        }

        issuedApiKey.setApiKeyName(createApiKey.apiKeyName());
        issuedApiKey.setHashedApiKey(createApiKey.hashedApiKey());
        issuedApiKey.setGoogleAccountApiKeyLink(createApiKey.googleAccountApiKeyLink());
        issuedApiKey.setIssuedAt(createApiKey.issuedAt());
        issuedApiKey.setRevoked(createApiKey.revoked());
        issuedApiKey.setExpiresAt(createApiKey.expiresAt());
        issuedApiKey.setLastUsedAt(createApiKey.lastUsedAt());

        return issuedApiKey;
    }


    public static ResponseApiKey mapToResponse(IssuedApiKey issuedApiKey) {
        if (issuedApiKey == null) {
            return null;
        }

        return new ResponseApiKey(
                issuedApiKey.getIssuedAt(),
                issuedApiKey.getExpiresAt(),
                issuedApiKey.getLastUsedAt(),
                issuedApiKey.getApiKeyName(),
                issuedApiKey.isRevoked()

        );

    }

    public static GenerateApiKeyRequest normalizeExpiresAt(GenerateApiKeyRequest createApiKey) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inputDate = createApiKey.expiresAt();

        if (inputDate != null && inputDate.isBefore(now)) {
            inputDate = LocalDateTime.now().plusMinutes(5);
        }

        if (inputDate != null && inputDate.isAfter(now.plusDays(15))) {
            inputDate = LocalDateTime.now().plusDays(15);
        }



        return new GenerateApiKeyRequest(createApiKey.apiKeyName(), inputDate);

    }
}
