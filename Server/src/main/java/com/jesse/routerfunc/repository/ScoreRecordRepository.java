package com.jesse.routerfunc.repository;

import com.jesse.routerfunc.dto.ScoreQueryDTO;
import com.jesse.routerfunc.entity.ScoreRecordEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScoreRecordRepository
        extends ReactiveCrudRepository<ScoreRecordEntity, Integer>
{
    /** 根据 ID 查找成绩。 */
    @Query(value =
            """
            SELECT score_id, user_id, user_name, submit_date,
                   correct_count, error_count, no_answer_count
            FROM score_record
            INNER JOIN users USING(user_id)
            WHERE score_id = :scoreId
            """
    )
    Mono<ScoreQueryDTO>
    findScoreRecordByScoreId(Integer scoreId);

    /** 按照时间排序，找出时间最近的 n 条成绩。 */
    @Query(value =
            """
            SELECT score_id, user_id, user_name, submit_date,
                   correct_count, error_count, no_answer_count
            FROM score_record
            INNER JOIN users USING(user_id)
            ORDER BY submit_date DESC
            LIMIT :n OFFSET 0
            """
    )
    Flux<ScoreQueryDTO>
    findRecentScoreRecord(Integer n);

    /**
     * 分页查询成绩。
     *
     * @param userName 查询用户名
     * @param limit    每页几条数据？
     * @param offset   偏移量，也可理解为第几页
     */
    @Query(value =
        """
        SELECT score_id, user_id, user_name, submit_date,
                   correct_count, error_count, no_answer_count
        FROM score_record
        INNER JOIN users USING(user_id)
        WHERE user_name = :userName
        ORDER BY submit_date DESC
        LIMIT :limit OFFSET :offset
        """)
    Flux<ScoreQueryDTO>
    findScoreRecordWithPagination(
        @Param(value = "userName") String userName,
        @Param(value = "limit")    int limit,
        @Param(value = "offset")   int offset
    );

    /** 获取总记录数（用于分页） */
    @Query(
        """
        SELECT COUNT(*)
        FROM score_record
        INNER JOIN users USING(user_id)
        WHERE user_name = :userName
        """)
    Mono<Long>
    countAllScoreRecordsByName(
        @Param(value = "userName") String userName
    );

    /** 清空整张成绩表。*/
    @Query(value = "TRUNCATE score_record")
    Mono<Void> truncateScoreRecord();
}