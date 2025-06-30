package com.jesse.routerfunc.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface QueryRequestInterface
{
    @NotNull Mono<ServerResponse>
    getScoreRecordById(ServerRequest request);

    @NotNull Mono<ServerResponse>
    getRecentScore(ServerRequest request);

    @NotNull Mono<ServerResponse>
    getScoreByPagination(ServerRequest request);

    @NotNull Mono<ServerResponse>
    insertNewScoreRecord(ServerRequest request);

    @NotNull Mono<ServerResponse>
    updateSpecifiedScoreRecord(ServerRequest request);

    @NotNull Mono<ServerResponse>
    truncateScoreRecord(ServerRequest ignore);

    @NotNull Mono<ServerResponse>
    deleteScoreRecordById(ServerRequest request);
}
