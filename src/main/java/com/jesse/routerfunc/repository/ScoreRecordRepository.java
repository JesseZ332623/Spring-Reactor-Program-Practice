package com.jesse.routerfunc.repository;

import com.jesse.routerfunc.dto.ScoreQueryDTO;
import com.jesse.routerfunc.entity.ScoreRecordEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScoreRecordRepository
        extends ReactiveCrudRepository<ScoreRecordEntity, Integer>
{
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

    @Query(value = "TRUNCATE score_record")
    Mono<Void> truncateScoreRecord();
}