package org.storkforge.barkr.domain;


import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.storkforge.barkr.infrastructure.persistence.GoogleAccountApiKeyLinkRepository;
import org.storkforge.barkr.infrastructure.persistence.IssuedApiKeyRepository;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"barkr.api.secret=test-value"})
class IssuedApiKeyServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:latest"));

    @MockitoBean
    private IssuedApiKeyRepository issuedApiKeyRepository;

    @Value("${barkr.api.secret}")
    private String secretKey;

    @MockitoBean
    private GoogleAccountApiKeyLinkRepository googleAccountApiKeyLinkRepository;

    @Autowired
    private IssuedApiKeyService service;


    @Test
    @DisplayName("testValue injection is working")
    void testValueInjectionIsWorking() {
        System.out.println(secretKey);
        assertThat(secretKey).isEqualTo("test-value");

    }

}
