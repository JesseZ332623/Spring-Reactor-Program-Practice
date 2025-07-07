package com.jesse.routerfunc;

import com.jesse.routerfunc.config.RouterFunctionConfig;
import com.jesse.routerfunc.dto.ScoreQueryDTO;
import com.jesse.routerfunc.entity.ScoreRecordEntity;
import com.jesse.routerfunc.repository.ScoreRecordRepository;
import com.jesse.routerfunc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.jesse.routerfunc.RandomTimeGenerator.randomBetween;
import static java.lang.String.format;
import static com.jesse.routerfunc.controller.utils.ResponseBuilder.APIResponse;

@Slf4j
@SpringBootTest
class RouterFuncApplicationTests
{
    @Value("${app.test.data-storage-path}")
    private String RESULT_STORAGE_PATH;

    @Autowired
    private ScoreRecordRepository scoreRecordRepository;

    @Autowired
    private UserRepository userRepository;

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

    /** 执行单条插入时的响应体类型。 */
    private final ParameterizedTypeReference<APIResponse<ScoreRecordEntity>>
        INSERT_RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

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
    public void TestSingleScoresQuery() throws IOException
    {

        Path savePath = Paths.get(RESULT_STORAGE_PATH);
        Files.deleteIfExists(savePath);

        final long SCORE_COUNT
            = Objects.requireNonNull(this.scoreRecordRepository.count().block());
        final int BUFFER_MAX = 100;

        Set<APIResponse<ScoreQueryDTO>> responseBuffer = new HashSet<>();

        for (long index = 1L; index <= 4000; index++)
        {
            long count = ThreadLocalRandom.current().nextLong(1, SCORE_COUNT);
            this.webTestClient.get()
                .uri(format("/api/query/score_record?id=%d", count))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SINGLE_QUERY_RESPONSE_TYPE)
                .value((response) -> {
                        responseBuffer.add(response);
                        if (responseBuffer.size() >= BUFFER_MAX)
                        {
                            responseBuffer.forEach(
                                (res) -> {
                                    try
                                    {
                                        Files.writeString(
                                            savePath, res.getData().toString() + "\n",
                                            StandardCharsets.UTF_8,
                                            StandardOpenOption.WRITE,
                                            StandardOpenOption.APPEND,
                                            StandardOpenOption.CREATE
                                        );
                                    }
                                    catch (IOException exception)
                                    {
                                        log.error(exception.getMessage());
                                        throw new RuntimeException(exception);
                                    }
                                }
                            );
                            responseBuffer.clear();
                        }
                    }
                );
        }
    }

    @Test
    public void TestRecentScoreQuery()
    {
        for (int index = 0; index < 3; ++index)
        {
            long count
                = ThreadLocalRandom.current().nextLong(1, 50);

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
        List<String> allUsers
            = this.userRepository.findAllUserName()
                  .collectList().block();

        Assertions.assertNotNull(allUsers);

        for (String userName : allUsers)
        {
            long totalPage
                = Objects.requireNonNull(
                    this.scoreRecordRepository.countAllScoreRecordsByName(userName).block()
            ) / 8;

            for (long page = 1; page <= totalPage; ++page)
            {
                this.webTestClient.get()
                    .uri(format("/api/query/paginate_score?name=%s&page=%d", userName, page))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PAGINATION_QUERY_RESPONSE_TYPE)
                    .value((response) -> {
                        var scores = response.getData();
                        scores.forEach(System.out::println);
                    });
            }
        }
    }

    @Test
    public void TestNewScoreGenerate()
    {
        if (this.scoreRecordRepository.count().block() > 0) {
             return;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        final int INSERT_AMOUNT = 500000;

        /*
         * 设计并行计划：
         * 池中有 36 个线程，队列长度 4500，执行线程名：Batch-Insert。
         */
        Scheduler scheduler
            = Schedulers.newBoundedElastic(
            36, 4500, "Batch-Insert"
        );

        Mono<Long> insertStream
            = Flux.range(0, INSERT_AMOUNT)
            .flatMap((number) ->
            {
                var score = new ScoreRecordEntity(
                    random.nextLong(1, 4),
                    randomBetween(
                        LocalDateTime.of(2021, 1, 1, 0, 0, 0),
                        LocalDateTime.now()
                    ),
                    random.nextInt(1, 30),
                    random.nextInt(1, 30),
                    random.nextInt(1, 30)
                );

                return this.scoreRecordRepository.save(score)
                    .subscribeOn(scheduler)
                    .thenReturn(1L);
            }).count()
            .doOnSuccess((count) ->
                log.info("Successfully inserted {} rows.", count)
            )
            .doOnError((exception) ->
                log.error("Insert failed! Cause: {}.", exception.getMessage())
            );

        StepVerifier.create(insertStream)
            .expectNext(Long.valueOf(INSERT_AMOUNT))
            .verifyComplete();
    }

    @Test
    public void TestNewScoreAppend()
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> scoreIds   = new ArrayList<>();

        Flux<APIResponse<ScoreRecordEntity>> entireStream
            = Flux.range(0, 100)
                  .concatMap((number) -> {
                        // 生产一个成绩实体。
                        var score = new ScoreRecordEntity(
                            random.nextLong(1, 4),
                            LocalDateTime.now(),
                            random.nextInt(1, 30),
                            random.nextInt(1, 30),
                            random.nextInt(1, 30)
                        );

                      // 发起请求并输出响应数据
                      APIResponse<ScoreRecordEntity> response
                          = this.webTestClient.post()
                                .uri("/api/add_new_score")
                                .bodyValue(score)
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(INSERT_RESPONSE_TYPE)
                                .returnResult()
                                .getResponseBody();

                      Assertions.assertNotNull(response);

                      return Flux.just(response);
                  })
            .doOnNext((response) -> {
                scoreIds.add(response.getData().getScoreId());
                System.out.println(response.getData().toString());
            }).doOnComplete(() -> log.info("Test insert request complete!"));

        Flux<Void> deleteStream
            = Flux.fromIterable(scoreIds).concatMap(
            (id) -> this.scoreRecordRepository.deleteById(id)
        )
        .doOnSubscribe((subscription) -> log.info("Clean test data..."))
        .doOnComplete(() -> log.info("Clean test data complete!"));

        StepVerifier.create(entireStream.thenMany(deleteStream)).verifyComplete();
    }
}