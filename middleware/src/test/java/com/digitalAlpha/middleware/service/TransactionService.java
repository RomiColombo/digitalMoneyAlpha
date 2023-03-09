package com.digitalAlpha.middleware.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@SuppressWarnings("all")
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN",username = "123456")
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class TransactionService {

    @Test
    public void abc() {

    }
}
