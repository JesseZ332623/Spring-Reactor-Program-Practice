package com.jesse.routerfunc.controller.impl;

import com.jesse.routerfunc.controller.TestRequestInterface;
import com.jesse.routerfunc.controller.utils.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class TestRequestComponent implements TestRequestInterface
{
    @Autowired
    private ResponseBuilder responseBuilder;

    /**
     * 对于从前端传来的 HTTP 请求实例，
     * 输出请求的所有属性。
     */
    private static void
    processAttributesMap(Map<String, Object> map)
    {
        map.forEach(
                (k, v) ->
                        System.out.println(k + ": " + v)
        );
    }

    /**
     * 处理 /hello 请求的方法。
     */
    public @NotNull Mono<ServerResponse>
    helloHandle(ServerRequest request)
    {
        processAttributesMap(request.attributes());

        return this.responseBuilder
                   .OK(
                       null,
                       "Use route function firstly.",
                       null, null
                   );
    }

    /**
     * 处理 /bye 请求的方法。
     */
    public @NotNull Mono<ServerResponse>
    byeHandle(ServerRequest request)
    {
        processAttributesMap(request.attributes());

        return this.responseBuilder.OK(
            null,"Bye!",
            null, null
        );
    }
}
