package org.storkforge.barkr.mapper;

import org.storkforge.barkr.dto.apiKeyDto.GenerateApiKeyRequest;

import java.time.LocalDateTime;

public class ApiKeyMapper {

    private ApiKeyMapper() {
    }

    public static GenerateApiKeyRequest normalizeExpiresAt(GenerateApiKeyRequest createApiKey) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inputDate = createApiKey.expiresAt();

        if (inputDate != null && inputDate.isBefore(now)) {
            inputDate = null;
        }

        if (inputDate != null && inputDate.isAfter(now.plusDays(15))) {
            inputDate = LocalDateTime.now().plusDays(15);
        }



        return new GenerateApiKeyRequest(createApiKey.apiKeyName(), inputDate);

    }
}
