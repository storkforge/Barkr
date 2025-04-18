package org.storkforge.barkr.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.storkforge.barkr.domain.entity.IssuedApiKey;
import org.storkforge.barkr.infrastructure.persistence.GoogleAccountApiKeyLinkRepository;
import org.storkforge.barkr.infrastructure.persistence.IssuedApiKeyRepository;

import java.util.Optional;

@Service
@Transactional
public class IssuedApiKeyService {

    private final Logger log = LoggerFactory.getLogger(IssuedApiKeyService.class);

    private final IssuedApiKeyRepository issuedApiKeyRepository;
    private final GoogleAccountApiKeyLinkRepository googleAccountApiKeyLinkRepository;


    public IssuedApiKeyService(IssuedApiKeyRepository issuedApiKeyRepository, GoogleAccountApiKeyLinkRepository googleAccountApiKeyLinkRepository) {
        this.issuedApiKeyRepository = issuedApiKeyRepository;
        this.googleAccountApiKeyLinkRepository = googleAccountApiKeyLinkRepository;

    }


    public Optional<IssuedApiKey> issuedApiKeyExists(String hashedApiKey) {
        log.info("Checking if issued api key exists");
        return issuedApiKeyRepository.findByHashedApiKey(hashedApiKey);
    }




    public void save(IssuedApiKey issuedApiKey) {
        issuedApiKeyRepository.save(issuedApiKey);
    }



}
