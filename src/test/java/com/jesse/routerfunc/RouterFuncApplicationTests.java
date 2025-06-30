package com.jesse.routerfunc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouterFuncApplicationTests
{
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldReturnRecentScores()
    {
        this.webTestClient.get()
            .uri("/api/recent_score?count=-1")
            .accept(MediaType.APPLICATION_JSON).exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.*").isArray()
            .jsonPath("$.data.length()").isEqualTo(5);
    }
}