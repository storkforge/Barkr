package org.storkforge.barkr.web.domain;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.storkforge.barkr.web.dto.FactResponse;

@Service
public class DogFactService {

  private final WebClient webClient;

  public DogFactService(WebClient webClient) {
    this.webClient = webClient;
  }

  @Retryable(backoff = @Backoff(delay = 500))
  public String getDogFact() {
    return webClient.get()
            .uri("/facts?limit=1")
            .retrieve()
            .bodyToMono(FactResponse.class)
            .map(response -> response
                    .data()
                    .getFirst()
                    .attributes()
                    .body()
            )
            .block();
  }

  @Recover
  public String recover() {
    return "Error when connecting";
  }
}
