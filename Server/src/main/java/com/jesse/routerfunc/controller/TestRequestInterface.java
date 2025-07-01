package com.jesse.routerfunc.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface TestRequestInterface
{
    @NotNull Mono<ServerResponse>
    helloHandle(ServerRequest request);

    @NotNull Mono<ServerResponse>
    byeHandle(ServerRequest request);
}
