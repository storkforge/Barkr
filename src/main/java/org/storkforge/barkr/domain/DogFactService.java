package org.storkforge.barkr.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.storkforge.barkr.dto.factDto.FactResponse;

@Service
public class DogFactService {
    private final Logger log = LoggerFactory.getLogger(DogFactService.class);

    private final WebClient webClient;

    public DogFactService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Retryable(backoff = @Backoff(delay = 500))
    //@Cacheable("dogFact")
    public String getDogFact() {
        log.info("Getting dog fact!");

        return webClient.get()
                .uri("/facts?limit=1")
                .retrieve()
                .toEntity(FactResponse.class)
                .block()
                .getBody()
                .data()
                .getFirst()
                .attributes()
                .body();
    }

    @Recover
    public String recover(Exception e) {
        log.error("Failed to fetch dog fact after retries", e);
        return "Unable to fetch a dog fact at the moment. Please try again later.";
    }
}
