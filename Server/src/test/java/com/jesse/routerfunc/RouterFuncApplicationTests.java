package com.jesse.routerfunc;

import com.jesse.routerfunc.config.RouterFunctionConfig;
import com.jesse.routerfunc.dto.ScoreQueryDTO;
import com.jesse.routerfunc.repository.ScoreRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.String.format;
import static com.jesse.routerfunc.controller.utils.ResponseBuilder.APIResponse;

@Slf4j
@SpringBootTest
class RouterFuncApplicationTests
{
    @Autowired
    private ScoreRecordRepository scoreRecordRepository;

    @Autowired
    private ApplicationContext context;

    private WebTestClient webTestClient;

    /** 执行单条查询时的响应体类型。 */
    private final ParameterizedTypeReference<APIResponse<ScoreQueryDTO>>
        SINGLE_QUERY_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    /** 执行最近 n 条查询时的响应体类型。 */
    private final ParameterizedTypeReference<APIResponse<List<ScoreQueryDTO>>>
        RECENT_QUERY_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    /** 执行分页查询时的响应体类型。 */
    private final ParameterizedTypeReference<APIResponse<List<ScoreQueryDTO>>>
        PAGINATION_QUERY_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    /**
     * 将 webTestClient 与一个路由函数绑定，
     * 为后续的测试做准备，这里几个要点解释一下。
     *
     * <ul>
     *     <li>
     *         {@literal @BeforeEach} 是 Jupiter 5 中的一个注解，
     *         表示在所有被 @Test 注解的方法执行之前，先调用本注解修饰的方法。
     *     </li>
     *     <li>
     *         本项目使用响应式组件编写，所以 webTestClient
     *         必须绑定一个路由函数，调用 ApplicationContext
     *         的 getBean() 方法，直接获取路由函数配置类。
     *     </li>
     * </ul>
     */
    @BeforeEach
    public void setup()
    {
        var routerFunctionConf
            = context.getBean(RouterFunctionConfig.class);

        webTestClient
            = WebTestClient.bindToRouterFunction(
                routerFunctionConf.startRouterFunction()
            ).build();
    }

    @Test
    public void TestSingleScoresQuery()
    {
        final long SCORE_COUNT
            = Objects.requireNonNull(this.scoreRecordRepository.count().block());

        for (long id = 1L; id <= SCORE_COUNT; id++)
        {
            this.webTestClient.get()
                .uri(format("/api/query/score_record?id=%d", id))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SINGLE_QUERY_RESPONSE_TYPE)
                .value((response) -> {
                        System.out.println(response.getData().toString());
                    }
                );
        }
    }

    @Test
    public void TestRecentScoreQuery()
    {
        final int paramCount = 50;
        final long SCORE_COUNT
            = Objects.requireNonNull(this.scoreRecordRepository.count().block());

        for (int index = 0; index < 3; ++index)
        {
            long count
                = ThreadLocalRandom.current().nextLong(1, 10);

            this.webTestClient.get()
                .uri(format(
                        "/api/query/recent_score?count=%d", count
                ))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RECENT_QUERY_RESPONSE_TYPE)
                .value((response) ->
                    {
                        var scores = response.getData();
                        scores.forEach(System.out::println);
                    }
                );
        }
    }

    @Test
    public void TestPaginationQuery()
    {
        int page = 1;
        final long totalPage
            = Objects.requireNonNull(
                this.scoreRecordRepository.count().block()
            ) / 8;

        do
        {
            this.webTestClient.get()
                .uri(format("/api/query/paginate_score?page=%d", page))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PAGINATION_QUERY_RESPONSE_TYPE)
                .value((response) -> {
                    var scores = response.getData();
                    scores.forEach(System.out::println);
                });

            ++page;
        }
        while (page <= totalPage);
    }
}