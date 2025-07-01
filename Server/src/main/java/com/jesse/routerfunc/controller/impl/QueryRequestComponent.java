package com.jesse.routerfunc.controller.impl;

import com.jesse.routerfunc.controller.QueryRequestInterface;
import com.jesse.routerfunc.controller.utils.Link;
import com.jesse.routerfunc.controller.utils.OperatorLogger;
import com.jesse.routerfunc.controller.utils.ResponseBuilder;
import com.jesse.routerfunc.dto.ScoreQueryDTO;
import com.jesse.routerfunc.dto.UpdateScoreDTO;
import com.jesse.routerfunc.entity.ScoreRecordEntity;
import com.jesse.routerfunc.repository.ScoreRecordRepository;
import com.jesse.routerfunc.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpMethod.GET;
import static com.jesse.routerfunc.controller.utils.URIParamPrase.praseNumberRequestParam;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;
import static com.jesse.routerfunc.controller.utils.ResponseBuilder.APIResponse;

@Slf4j
@Component
public class QueryRequestComponent implements QueryRequestInterface
{
    @Autowired
    private ScoreRecordRepository scoreRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OperatorLogger  operatorLogger;

    @Autowired
    private ResponseBuilder responseBuilder;

    @Value(value = "${server.address}")
    private String SERVER_ADDRESS;

    @Value(value = "${server.port}")
    private int SERVER_PORT;

    /** 一页固定 15 条数据 */
    private final int PAGE_LIMIT = 5;

    /** 请求根目录字符串。*/
    private String URI_ROOT;

    /** 成绩总数查询缓存有效期：30 秒。*/
    private final Duration SCORE_COUNT_CACHE_TTL = Duration.ofSeconds(30);

    /** 缓存的成绩表数据总条数。 */
    private Mono<Long> cachedScoreCount;

    /**
     * 在类构建时初始化 cachedScoreCount。
     * 使用 @PostConstruct 注解，确保该方法在依赖注入完成后才被执行、
     */
    @PostConstruct
    private void initScoreCount() {
        refreshScoreCountCache();
    }

    /** 在依赖注入完毕后组合出请求根目录。 */
    @PostConstruct
    private void initURIBase() {
        this.URI_ROOT = "http://" + SERVER_ADDRESS + ":" + SERVER_PORT;
    }

    /** 刷新缓存的核心方法。*/
    private void refreshScoreCountCache()
    {
        this.cachedScoreCount = this.scoreRecordRepository.count()
            .cache(SCORE_COUNT_CACHE_TTL)
            .doOnSuccess(count -> log.debug("Score count updated: {}", count))
            .doOnError(e  -> log.error("Failed to refresh score count cache", e));
    }

    /** 在数据变更操作后刷新缓存。*/
    private void onDataChanged()
    {
        log.info("Data changed, refreshing count cache.");
        refreshScoreCountCache();
    }

    private @NotNull Mono<ServerResponse>
    queryScoreByIdHandle(Integer scoreId)
    {
        return this.scoreRecordRepository
                   .findScoreRecordByScoreId(scoreId)
                   .flatMap((score) -> {

                       final String singleQueryLink = URI_ROOT + "/api/query/score_record?id=";

                       return this.cachedScoreCount
                                  .flatMap((count) -> {
                                       Set<Link> hateOASLink = Set.of(
                                           new Link("next",
                                               (scoreId + 1 <= count)
                                                   ? singleQueryLink + (scoreId + 1)
                                                   : singleQueryLink + count, GET
                                           ),
                                           new Link("prev",
                                               (scoreId - 1 >= 1)
                                                   ? singleQueryLink + (scoreId - 1)
                                                   : singleQueryLink + 1, GET
                                           ),
                                           new Link("first", singleQueryLink + "1", GET),
                                           new Link("last", singleQueryLink + count, GET)
                                       );

                                       return this.responseBuilder.OK(
                                           score,
                                           format("Query score record id = %d success!", scoreId),
                                           null, hateOASLink
                                       );
                               });
                   }
                   )
                   .doOnSubscribe((subscription) ->
                            this.operatorLogger.logStartedOperator(
                                    "Processing score query by id: " + scoreId
                            )
                    )
                   .doOnError((error) ->
                            this.operatorLogger.logErrorOperator(
                                    "Query score by id: "    +
                                    scoreId + "failed!", error
                            )
                    );
    }

    /**
     * 通过前端 URI 中的 id 参数，来查询表中对应的成绩记录。
     *
     * @return 承载了单个 ServerResponse 的响应式流，由 Spring 负责订阅。
     */
    public @NotNull Mono<ServerResponse>
    getScoreRecordById(ServerRequest request)
    {
        return Mono.defer(
                () -> {
                    Integer queryScoreId
                            = praseNumberRequestParam(request, "id");

                    return this.scoreRecordRepository.existsById(queryScoreId).flatMap(
                                (exists) ->
                                        (exists) ? queryScoreByIdHandle(queryScoreId)
                                                 : this.responseBuilder.NOT_FOUND(
                                                         format("Score id = %d not found!", queryScoreId),
                                                         null
                                                 )
                            );
                }
        ).onErrorResume(
                IllegalArgumentException.class,
                (exception) ->
                    this.responseBuilder.BAD_REQUEST(
                        format(
                            "URI %s exception cause: %s.",
                            request.uri(), exception.getMessage()
                        ),
                        exception
                    )
        );
    }

    /**
     * 查询时间最新的 count 条成绩（由前端 URI 的 count 参数指示）。
     */
    public @NotNull Mono<ServerResponse>
    getRecentScore(ServerRequest request)
    {
        return Mono.defer(
                () -> {
                    // 先检查前端传入的参数值有没有大于总数据条数。
                    return this.cachedScoreCount.flatMap((scoreCount) ->
                    {
                        Integer count
                            = Math.toIntExact(
                                Math.min(
                                    praseNumberRequestParam(request, "count"),
                                    scoreCount
                                )
                            );

                        /*
                         * 数据的流动如下所示：
                         *
                         * - findRecentScoreRecord() 找出表中时间最新的 count 条数据（返回 Flux<ScoreRecordEntity>）
                         * - collectList()           将 Flux<ScoreRecordEntity> 流中的多个数据收集为 Mono<List<ScoreRecordEntity>>
                         * - flatMap(lamba)          将 Mono<List<ScoreRecordEntity>>，流内的数据映射给 ServerResponse，
                         *                              然后返回 Mono<ServerResponse>，数据的处理到这里就完成了
                         * - doOnSubscribe()         在该 Mono 正式被订阅前，加一条开始处理数据的日志输出
                         * - doOnError()             定义 Mono 被订阅执行后出错时，输出的日志
                         */
                        return scoreRecordRepository.findRecentScoreRecord(count)
                            .collectList()
                            .flatMap(records ->
                                (!records.isEmpty())
                                    ? this.responseBuilder.OK(
                                    records,
                                    format("Query %d rows.", count),
                                    null, null
                                )
                                    : this.responseBuilder.NOT_FOUND("Recent score not found!", null)
                            )
                            .doOnSubscribe(subscription ->
                                this.operatorLogger
                                    .logStartedOperator("Processing recent scores request.")
                            )
                            .doOnSuccess((response)
                                ->this.operatorLogger.logSuccessOperator(
                                    format("Query %d rows.", count)
                                )
                            )
                            .doOnError(error ->
                                this.operatorLogger.logErrorOperator(
                                    "Error fetching scores!", error
                                )
                            );
                    });
                }
        ).onErrorResume(
                IllegalArgumentException.class,
                (exception) ->
                    this.responseBuilder.BAD_REQUEST(
                        format(
                            "URI %s exception cause: %s.",
                            request.uri(), exception.getMessage()
                        ),
                        exception
                    )
        );
    }

    public @NotNull Mono<ServerResponse>
    getScoreByPagination(ServerRequest request)
    {
        return Mono.defer(
            () -> {
                int page = praseNumberRequestParam(request, "page");

                int springPage = page > 0 ? page - 1 : 0;
                
                return this.cachedScoreCount.flatMap(
                    (count) -> {
                        // 内部手动组装响应体
                        return this.scoreRecordRepository
                            .findScoreRecordWithPagination(PAGE_LIMIT, springPage * PAGE_LIMIT)
                            .collectList()
                            .flatMap(
                                (scores) -> {
                                    APIResponse<List<ScoreQueryDTO>> response = new APIResponse<>(OK);

                                    Set<Link> links = getLinks(count, page);

                                    response.withPagination(page, PAGE_LIMIT, count);

                                    for (Link link : links)
                                    {
                                        response.withLink(
                                            link.getRel(), link.getHref(), link.getMethod()
                                        );
                                    }

                                    response.setData(scores);
                                    response.setMessage(
                                        format(
                                            "Query score record (Page = %d, Size = %d) complete!",
                                            page, PAGE_LIMIT
                                        )
                                    );

                                    return this.responseBuilder.build(
                                        (headers) ->
                                            headers.setContentType(MediaType.APPLICATION_JSON),
                                        response
                                    );
                                }
                            )
                            .doOnSubscribe((subscription) ->
                                this.operatorLogger.logStartedOperator(
                                    format(
                                        "Ready to query with pagination! (Page = %d, Size = %d)",
                                        page, PAGE_LIMIT
                                    )
                                )
                            )
                            .doOnSuccess((response) ->
                                this.operatorLogger.logSuccessOperator(
                                    format(
                                        "Query score record (Page = %d, Size = %d) complete!",
                                        page, PAGE_LIMIT
                                    )
                                )
                            )
                            .doOnError(
                                (error) ->
                                    this.operatorLogger.logErrorOperator(
                                    "Failed to query with pagination! Cause: ", error
                                )
                            );
                    }
                );
            }
        ).onErrorResume(
            IllegalArgumentException.class,
            (exception) ->
                this.responseBuilder.BAD_REQUEST(
                    format(
                        "URI %s exception cause: %s.",
                        request.uri(), exception.getMessage()
                    ),
                    exception
                )
        );
    }

    private @NotNull Set<Link> getLinks(Long count, int page)
    {
        String paginateQueryURI = URI_ROOT + "/api/query/paginate_score?";

        long totalPage
            = (count % PAGE_LIMIT == 0)
            ? count / PAGE_LIMIT : count / PAGE_LIMIT + 1;

        return Set.of(
            new Link(
                "next page",
                paginateQueryURI + "page=" +
                    ((page + 1 > totalPage) ? totalPage : page + 1),
                GET
            ),
            new Link(
                "prev page",
                paginateQueryURI + "page=" +
                    (Math.max(page - 1, 1)),
                GET
            ),
            new Link(
                "first page",
                paginateQueryURI + "page=1", GET
            ),
            new Link(
                "last page",
                paginateQueryURI + "page=" + totalPage, GET
            )
        );
    }

    private @NotNull Mono<ServerResponse>
    doInsertNewScore(ScoreRecordEntity newScore)
    {
        return this.scoreRecordRepository.save(newScore)
            .flatMap((flatScore) -> {
                /*
                 * 对于新增数据操作，
                 * 这里最好是返回 201 (created) 响应码，定位到新增的资源处。
                 */
                URI newResourceLocation
                    = URI.create(
                    "http://" + SERVER_ADDRESS + ":" + SERVER_PORT +
                        "/api/query/score_record?id=" +
                        newScore.getScoreId()
                );

                log.debug("New resource location: {}", newResourceLocation);

                return this.responseBuilder
                    .CREATED(
                        newResourceLocation,
                        format("Insert new score record id = %d complete.", newScore.getScoreId()),
                        newScore, null
                    );
            })
            .doOnSubscribe((subscription) ->
                this.operatorLogger
                    .logStartedOperator("Ready to insert new score record.")
            )
            .doOnSuccess(response -> {
                    this.onDataChanged();
                    this.operatorLogger
                        .logSuccessOperator("Insert new score record complete!.");
                }
            )
            .doOnError((error) ->
                this.operatorLogger
                    .logErrorOperator("Insert new score record failed!", error)
            );
    }

    /**
     * 根据前端提交的 JSON（通过 request.bodyToMono({@literal Class<?>}) 映射成指定实体），
     * 插入一条新的成绩记录。
     */
    @Transactional
    public @NotNull Mono<ServerResponse>
    insertNewScoreRecord(ServerRequest request)
    {
        return request.bodyToMono(ScoreRecordEntity.class)
            .flatMap((score) ->
            {
                // 先检查用户表中有没有这个 ID，再进行后续操作
                return this.userRepository.existsById(score.getUserId())
                    .flatMap(
                        (exists) ->
                            (exists) ? this.doInsertNewScore(score)
                                     : this.responseBuilder.NOT_FOUND(
                                         format(
                                             "Insert failed! User ID = %d not found!",
                                             score.getUserId()
                                         ), null
                                    )
                    );
            })
            .onErrorResume(
                IllegalArgumentException.class,
                (exception) -> {
                    /*
                     * 若前端给出的 JSON 不正确，
                     * 那么 bodyToMono(ScoreRecordEntity.class) 便不会正确解析，
                     * 后续的调用会抛出非法参数异常。
                     */
                    return this.responseBuilder.BAD_REQUEST(
                        exception.getMessage(),
                        exception
                    );
                }
            );
    }

    private @NotNull Mono<ServerResponse>
    doUpdateScoreRecord(UpdateScoreDTO updateScoreDTO)
    {
        return this.scoreRecordRepository
                   .findById(updateScoreDTO.getScoreId())
                   .flatMap(
                       (score) ->
                       {
                           score.setCorrectCount(
                               updateScoreDTO.getCorrectCount()
                           );

                           score.setErrorCount(
                               updateScoreDTO.getErrorCount()
                           );

                           score.setNoAnswerCount(
                               updateScoreDTO.getNoAnswerCount()
                           );

                           score.setSubmitDate(LocalDateTime.now());

                           return this.scoreRecordRepository.save(score);
                       }
                   )
                   .flatMap((updatedScore) ->
                   {
                       URI updatedResourceLocation
                           = URI.create(
                                   "http://" + SERVER_ADDRESS + ":" + SERVER_PORT +
                                   "/api/query/score_record?id=" + updatedScore.getScoreId()
                               );

                       return this.responseBuilder.CREATED(
                           updatedResourceLocation,
                           format("Update score id = %d complete!", updatedScore.getScoreId()),
                           updatedScore, null
                       );
                   })
                    .doOnSubscribe((subscription) ->
                        this.operatorLogger
                            .logStartedOperator("Ready to update score record.")
                    )
                    .doOnSuccess(response ->
                        this.operatorLogger
                            .logSuccessOperator("Update score record complete!")
                    )
                    .doOnError((error) ->
                        this.operatorLogger
                            .logErrorOperator("Update score record failed!", error)
                    );
    }

    @Transactional
    public @NotNull Mono<ServerResponse>
    updateSpecifiedScoreRecord(ServerRequest request)
    {
        return request.bodyToMono(UpdateScoreDTO.class)
                      .flatMap((updateScoreDTO) ->
                      {
                          // 检查成绩 ID 是否存在再进行修改
                          return this.scoreRecordRepository
                                     .existsById(updateScoreDTO.getScoreId())
                                     .flatMap((exists) ->
                                          (exists) ? this.doUpdateScoreRecord(updateScoreDTO)
                                                   : this.responseBuilder.NOT_FOUND(
                                                       format(
                                                           "Score ID = %d not found!",
                                                           updateScoreDTO.getScoreId()
                                                       ), null
                                          )
                                     );
                      })
                      .onErrorResume(
                          IllegalArgumentException.class,
                          (exception) -> {
                              /*
                               * 若前端给出的 JSON 不正确，
                               * 那么 bodyToMono(UpdateScoreDTO.class) 便不会正确解析，
                               * 后续的调用会抛出非法参数异常。
                               */
                              return this.responseBuilder.BAD_REQUEST(
                                  exception.getMessage(),
                                  exception
                              );
                          }
                      );
    }

    public @NotNull Mono<ServerResponse>
    truncateScoreRecord(ServerRequest ignore)
    {
        return this.scoreRecordRepository
                   .truncateScoreRecord()
                   .doOnSubscribe((subscription) ->
                            log.info("Ready to truncate score record."))
                   .doOnSuccess(response ->
                            log.info("Truncate score record completed successfully.")
                    )
                   .doOnError((error) ->
                            log.error(
                                    "Truncate score record failed! Cause: {}.",
                                    error.getMessage()
                            )
                    )
                    /*
                     * Mono / Flux 的 then() 操作部分注释原文如下：
                     * In other words, ignore element from this Mono and
                     * transform its completion signal into the emission and completion signal of a provided Mono<V>.
                     * Error signal is replayed in the resulting Mono<V>.
                     *
                     * 译文：
                     * 换言之，忽略此 Mono 中的元素，
                     * 并将其完成信号转换为提供的 Mono<V> 的发射事件和完成信号。
                     * 错误信号会在结果 Mono<V> 中原样传播。
                     *
                     * 在本例中，数据的流动如下所示：
                     * truncateScoreRecord() -> doOnSubscribe() -> doOnSuccess() -> doOnError()
                     * 它们都返回 Mono<Void>，
                     * then() 操作舍弃 Mono<Void> 的数据（Void 也没什么好拿的），
                     * 只关注操作完成的信号。
                     * 当上游完成信号发出时 then() 操作触发，构造出一个 OK 的响应。
                    */
                   .then(this.responseBuilder.OK(
                       null,
                       "Truncate score_record table complete!",
                       null, null
                   ));
    }

    /**
     * 根据前端 URI 的 id 参数，删除指定的成绩记录。
     */
    @Transactional
    public @NotNull Mono<ServerResponse>
    deleteScoreRecordById(ServerRequest request)
    {
        /*
         * Mono.defer() 推迟方法
         * 令代码块内的操作不会在构建 Mono 时就立即执行，而是在正式被订阅后才开始执行。
         *
         * 在本例中，倘若没有使用 defer()，
         * praseNumberRequestParam() 会立即执行，如果抛出异常就不会在数据管道中传递
         * （虽然可以在内部进行 try-catch 捕获，但这显然破环了响应式流）。
         *
         * 反之使用 defer() 再辅以 onErrorResume()（出错时恢复），
         * 订阅后执行 praseNumberRequestParam() 抛出的异常就会在数据管道中传递，
         * 被正确捕获并处理。
         */
        return Mono.defer(
                () -> {
                    Integer deletedId
                            = praseNumberRequestParam(request, "id");
                    return handleDeletion(deletedId);
                }
        ).onErrorResume(
                IllegalArgumentException.class,
                (exception) ->
                    this.responseBuilder.BAD_REQUEST(
                        format(
                            "URI %s exception cause: %s.",
                            request.uri(), exception.getMessage()
                        ),
                        exception
                    )
        );
    }

    private Mono<ServerResponse>
    handleDeletion(Integer scoreId)
    {
        return this.scoreRecordRepository.existsById(scoreId)
                .flatMap((exists) ->
                        (exists) ? deleteScoreRecord(scoreId)
                                 : this.responseBuilder.NOT_FOUND(
                                           format("Score id: [%d] not exist!", scoreId),
                                            null
                                       )
                );
    }

    private Mono<ServerResponse>
    deleteScoreRecord(Integer scoreId)
    {
        return this.scoreRecordRepository.deleteById(scoreId)
                .doOnSubscribe((X_x) ->
                        this.operatorLogger
                            .logStartedOperator("Ready to delete score record id = " + scoreId)
                )
                .doOnSuccess((o_O) -> {
                        this.onDataChanged();
                        this.operatorLogger
                            .logSuccessOperator(
                                "Delete core record id = " +
                                    scoreId + " completed!"
                            );
                    }
                )
                .doOnError((error) ->
                        this.operatorLogger.logErrorOperator(
                                "Delete score record id = " +
                                scoreId + " failed!", error
                        )
                )
                .then(this.responseBuilder.OK(
                    null,
                    format("Delete core record id = %d complete!", scoreId),
                    null, null
                ));
    }
}
