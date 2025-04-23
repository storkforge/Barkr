package org.storkforge.barkr.mapper;

import org.storkforge.barkr.domain.entity.IssuedApiKey;
import org.storkforge.barkr.dto.apiKeyDto.CreateApiKey;
import org.storkforge.barkr.dto.apiKeyDto.GenerateApiKeyRequest;
import org.storkforge.barkr.dto.apiKeyDto.ResponseApiKey;
import org.storkforge.barkr.dto.apiKeyDto.UpdateApiKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        issuedApiKey.setReferenceId(createApiKey.referenceId());

        return issuedApiKey;
    }


    public static ResponseApiKey mapToResponse(IssuedApiKey issuedApiKey) {
        if (issuedApiKey == null) {
            return null;
        }

        return new ResponseApiKey(
                formatDateAndTime(issuedApiKey.getIssuedAt()),
                formatDateAndTime(issuedApiKey.getExpiresAt()),
                formatDateAndTime(issuedApiKey.getLastUsedAt()),
                issuedApiKey.getApiKeyName(),
                issuedApiKey.isRevoked(),
                issuedApiKey.getReferenceId()

        );

    }

    public static GenerateApiKeyRequest normalizeExpiresAt(GenerateApiKeyRequest createApiKey) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inputDate = createApiKey.expiresAt();

        if (inputDate == null) {
            inputDate = LocalDateTime.now().plusMinutes(5);
        }

        if (inputDate != null && inputDate.isBefore(now)) {
            inputDate = LocalDateTime.now().plusMinutes(5);
        }

        if (inputDate != null && inputDate.isAfter(now.plusDays(15))) {
            inputDate = LocalDateTime.now().plusDays(15);
        }



        return new GenerateApiKeyRequest(createApiKey.apiKeyName(), inputDate);

    }

    public static String formatDateAndTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        }
        return "";
    }

    public static IssuedApiKey updateIssuedApiKey(IssuedApiKey issuedApiKey, UpdateApiKey updateApiKey) {
        if (issuedApiKey == null || updateApiKey == null || issuedApiKey.getApiKeyName() == null) {
            return null;
        }

        if (updateApiKey.revoke() != null) {
            issuedApiKey.setRevoked(updateApiKey.revoke());
        }

        if (updateApiKey.apiKeyName() != null) {
            issuedApiKey.setApiKeyName(updateApiKey.apiKeyName());
        }

        if(updateApiKey.lastUsedAt() != null) {
            issuedApiKey.setLastUsedAt(updateApiKey.lastUsedAt());
        }
        return issuedApiKey;

    }

}
