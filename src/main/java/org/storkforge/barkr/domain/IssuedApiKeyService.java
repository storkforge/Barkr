package org.storkforge.barkr.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.entity.IssuedApiKey;
import org.storkforge.barkr.dto.apiKeyDto.CreateApiKey;
import org.storkforge.barkr.dto.apiKeyDto.GenerateApiKeyRequest;
import org.storkforge.barkr.dto.apiKeyDto.ResponseApiKeyList;
import org.storkforge.barkr.dto.apiKeyDto.UpdateApiKey;
import org.storkforge.barkr.exceptions.AccountNotFound;
import org.storkforge.barkr.exceptions.IssuedApiKeyNotFound;
import org.storkforge.barkr.infrastructure.persistence.GoogleAccountApiKeyLinkRepository;
import org.storkforge.barkr.infrastructure.persistence.IssuedApiKeyRepository;
import org.storkforge.barkr.mapper.ApiKeyMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class IssuedApiKeyService {

    private final Logger log = LoggerFactory.getLogger(IssuedApiKeyService.class);

    private final IssuedApiKeyRepository issuedApiKeyRepository;
    private final GoogleAccountApiKeyLinkRepository googleAccountApiKeyLinkRepository;
    @Value("${barkr.api.secret}")
    private String secretKey;


    @Autowired
    public IssuedApiKeyService(
            IssuedApiKeyRepository issuedApiKeyRepository,
            GoogleAccountApiKeyLinkRepository googleAccountApiKeyLinkRepository) {
        this.issuedApiKeyRepository = issuedApiKeyRepository;
        this.googleAccountApiKeyLinkRepository = googleAccountApiKeyLinkRepository;

    }

    public Optional<GoogleAccountApiKeyLink> findByGoogleOidc2Id(String name) {
        return Optional.ofNullable(googleAccountApiKeyLinkRepository.findByAccount_GoogleOidc2Id(name).orElseThrow(() -> new AccountNotFound("Account was not found")));

    }


    public Optional<IssuedApiKey> issuedApiKeyExists(String hashedApiKey) {
        log.info("Checking if issued api key exists");
        return issuedApiKeyRepository.findByHashedApiKey(hashedApiKey);
    }

    public void apiKeyGenerate(GenerateApiKeyRequest request, String hashedApiKey) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        var googleOidc2id = authentication.getName();
        var link = googleAccountApiKeyLinkRepository.findByAccount_GoogleOidc2Id(googleOidc2id).orElseThrow(() -> new AccountNotFound("Account was not found! "));
        LocalDateTime issueDate = LocalDateTime.now();

        CreateApiKey createApiKey = new CreateApiKey(
                hashedApiKey,
                issueDate,
                request.expiresAt(),
                false,
                issueDate,
                request.apiKeyName(),
                link,
                generateUuid()
        );

        log.info("Adding new api key to account");

        IssuedApiKey key = ApiKeyMapper.mapToEntity(createApiKey);

        if(key != null)
            issuedApiKeyRepository.save(key);

    }

    public String generateRawApiKey() {
        byte[] lengthOfKey = new byte[32];
        SecureRandom random = new SecureRandom();
        random.nextBytes(lengthOfKey);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(lengthOfKey);


    }

    public UUID generateUuid() {
        Optional<IssuedApiKey> uuidFound;
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
            uuidFound = issuedApiKeyRepository.findByReferenceId(uuid);
        }while(uuidFound.isPresent());

        return uuid;
    }


    public String hashedApiKey(String rawApiKey) throws NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);

        byte[] hmac = mac.doFinal(rawApiKey.getBytes());
        return Base64.getEncoder().encodeToString(hmac);

    }

    public boolean apiKeyExists(String hashedApiKey) {
        log.info("Checking if issued api key exists");
        return issuedApiKeyRepository.findByHashedApiKey(hashedApiKey).isPresent();
    }

    public boolean apiKeyValidation(String rawApiKey) throws NoSuchAlgorithmException, InvalidKeyException {
        var hashedApikey = hashedApiKey(rawApiKey);
        var keyFound = issuedApiKeyRepository.findByHashedApiKey(hashedApikey);
        return keyFound.isPresent() && !keyFound.get().isRevoked();

    }

    public ResponseApiKeyList allApiKeys(String googleOidc2Id) {

        var recordsRemoved = revokeExpiredApiKeys();
        log.info("Removed {} api keys", recordsRemoved);

        var link = googleAccountApiKeyLinkRepository.findByAccount_GoogleOidc2Id(googleOidc2Id).orElseThrow(() -> new AccountNotFound("Account was not found! "));
        var apiKeys = issuedApiKeyRepository.findByGoogleAccountApiKeyLinkOrderByIssuedAtDesc(link);

        var apiKeyResponse = apiKeys.stream().map(ApiKeyMapper::mapToResponse).toList();
        return new ResponseApiKeyList(apiKeyResponse);

    }


    public void updateApiKey(UpdateApiKey updateApiKey) {
        var apikey = issuedApiKeyRepository.findByReferenceId(updateApiKey.referenceId()).orElseThrow(() -> new IssuedApiKeyNotFound("No api key was found"));
        var updateApikey = ApiKeyMapper.updateIssuedApiKey(apikey, updateApiKey);
        issuedApiKeyRepository.save(updateApikey);

    }

    public int revokeExpiredApiKeys() {
        return issuedApiKeyRepository.revokeExpiredKeys(LocalDateTime.now());
    }

    public boolean isValidUuid(UUID uuid, String googleOidc2Id) {
        var allApiKeys = allApiKeys(googleOidc2Id);
        return allApiKeys.apiKeys().stream()
                .anyMatch(k -> k.referenceId().equals(uuid));

    }





}
