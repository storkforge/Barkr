package org.storkforge.barkr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.storkforge.barkr.domain.entity.GoogleAccountApiKeyLink;
import org.storkforge.barkr.domain.entity.Post;
import org.springframework.boot.CommandLineRunner;
import org.storkforge.barkr.domain.entity.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.storkforge.barkr.domain.roles.BarkrRole;
import org.storkforge.barkr.infrastructure.persistence.PostRepository;
import org.storkforge.barkr.infrastructure.persistence.AccountRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@Profile("!test")
@EnableCaching
public class Config {
  private final Logger log = LoggerFactory.getLogger(Config.class);

  @Bean
  CommandLineRunner accountInit(AccountRepository accountRepository, PostRepository postRepository) {
    return _ -> {
      if (accountRepository.count() == 0 && postRepository.count() == 0) {
        log.info("Database is empty. Starting seeding process...");

        log.info("Seeding users/accounts into the database...");
        Account accountOne = new Account();
        accountOne.setUsername("Bella Pawkins");
        accountOne.setBreed("Golden Retriever");
        accountOne.setGoogleOidc2Id("1");
        accountOne.setImage(null);
        accountOne.setRoles(new HashSet<>(Set.of(BarkrRole.USER)));

        Account accountTwo = new Account();
        accountTwo.setUsername("Charlie Barkson");
        accountTwo.setBreed("Siberian Husky");
        accountTwo.setGoogleOidc2Id("2");
        accountTwo.setImage(null);
        accountTwo.setRoles(new HashSet<>(Set.of(BarkrRole.USER)));

        Account accountThree = new Account();
        accountThree.setUsername("Max Woofington");
        accountThree.setBreed("German Shepherd");
        accountThree.setGoogleOidc2Id("3");
        accountThree.setImage(null);
        accountThree.setRoles(new HashSet<>(Set.of(BarkrRole.USER)));

        GoogleAccountApiKeyLink link1 = new GoogleAccountApiKeyLink();
        link1.setAccount(accountOne);
        accountOne.setGoogleAccountApiKeyLink(link1);

        GoogleAccountApiKeyLink link2 = new GoogleAccountApiKeyLink();
        link2.setAccount(accountTwo);
        accountTwo.setGoogleAccountApiKeyLink(link2);

        GoogleAccountApiKeyLink link3 = new GoogleAccountApiKeyLink();
        link3.setAccount(accountThree);
        accountThree.setGoogleAccountApiKeyLink(link3);


        accountRepository.saveAll(List.of(accountOne, accountTwo, accountThree));

        log.info("Seeding posts... (requires users to exist first)");
        Post postOne = new Post();
        postOne.setContent("Just chased my tail for 10 minutes straight. New personal record! #TailChaser #DogLife");
        postOne.setAccount(accountOne);

        Post postTwo = new Post();
        postTwo.setContent("Got a new toy today! It squeaks and everything. Best. Day. Ever. #NewToy #HappyPup");
        postTwo.setAccount(accountTwo);

        Post postThree = new Post();
        postThree.setContent("Beach day with my humans! The waves were a bit scary but I was brave and fetched the ball from the water. Who else loves swimming?");
        postThree.setAccount(accountThree);

        postRepository.saveAll(List.of(postOne, postTwo, postThree));
        log.info("Database seeding completed successfully.");
      } else {
        log.info("Database already contains data. Skipping seed process to prevent duplicates.");
      }
    };
  }

  @Bean
  WebClient webClient() {
    return WebClient.builder().baseUrl("https://dogapi.dog/api/v2").build();
  }
}
