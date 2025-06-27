package com.jesse.routerfunc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@SpringBootTest
class RouterFuncApplicationTests
{
    @Test
    void TestFLatMapOperator()
    {
        Flux<Player> playerFlux
            = Flux.just(
                "Peter Griffin", "Lois Griffin",
                       "Cris Griffin", "Meg Griffin"
            ).flatMap(
               (name) -> {
                   String[] splitName = name.split("\\s");

                   return Mono.just(new Player(splitName[0], splitName[1]))
                              .subscribeOn(Schedulers.parallel());
               }
            );

        playerFlux.subscribe((player) -> log.info("{}", player));
    }
}