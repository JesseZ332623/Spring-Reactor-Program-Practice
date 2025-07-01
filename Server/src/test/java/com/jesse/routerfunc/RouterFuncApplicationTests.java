package com.jesse.routerfunc;

import com.jesse.routerfunc.dto.ScoreQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class RouterFuncApplicationTests
{
    @Autowired
    private WebClient webClient;

    @Test
    public void TestRecentScoresQuery()
    {
        Flux<ScoreQueryDTO> queryFlux
            = this.webClient.get()
                  .uri("/api/query/recent_score?count=5")
                  .accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .bodyToFlux(ScoreQueryDTO.class);

        queryFlux.blockLast();

        queryFlux.subscribe(System.out::println);
    }
}