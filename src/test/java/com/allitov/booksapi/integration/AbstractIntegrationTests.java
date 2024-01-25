package com.allitov.booksapi.integration;

import com.allitov.booksapi.model.repository.BookRepository;
import com.allitov.booksapi.model.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("classpath:db/init.sql")
@Transactional
@Testcontainers
public abstract class AbstractIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected BookService bookService;

    @Autowired
    protected BookRepository bookRepository;

    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:16");

    protected static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:7.2.4"));

    static {
        POSTGRESQL_CONTAINER.withReuse(true);
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.withExposedPorts(6379);
        REDIS_CONTAINER.withReuse(true);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @BeforeEach
    public void beforeEach() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
