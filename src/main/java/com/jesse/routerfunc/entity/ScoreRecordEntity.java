package com.jesse.routerfunc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * <p>score_record 数据表实体类。</p>
 *
 * <div>
 *     <p>这里用到了两个新的 Lombok 注解，这里说明一下：</p>
 *     <ol>
 *         <li>
 *             {@literal @RequiredArgsConstructor} 注解
 *             （为被需要的字段生成构造函数）
 *             <p>所谓 “被需要的字段” 指的是满足以下两个条件的字段：</p>
 *             <ol>
 *                 <li>被 {@literal @NonNull} 注解的字段。</li>
 *                 <li>没有被 final 关键字修饰且未初始化的字段</li>
 *             </ol>
 *         </li>
 *         <li>
 *             {@literal @EqualsAndHashCode} 注解
 *             （按照指定的规则生成 equals() 和 hashcode() 方法）
 *             <p>
 *                 这里设置了 exclude （排除）属性为 "scoreId"，
 *                 表明比较和哈希计算会排除 scoreId 字段。</br>
 *
 *             </p>
 *         </li>
 *     </ol>
 * </div>
 */
@Data
@Table(name = "score_record")
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "scoreId")
public class ScoreRecordEntity
{
    @Id
    @Column("score_id")
    private Integer scoreId;            // 成绩记录 ID

    @Column("user_id")
    private @NonNull Integer userId;             // 用户 ID

    @Column("submit_date")
    private @NonNull
    LocalDateTime submitDate = LocalDateTime.now();   // 成绩提交日期

    @Column("correct_count")
    private @NonNull
    Integer correctCount = 0;       // 正确数

    @Column("error_count")
    private @NonNull
    Integer errorCount = 0;         // 错误数

    @Column("no_answer_count")
    private @NonNull
    Integer noAnswerCount = 0;      // 未答数

    @Override
    public String toString()
    {
        return String.format(
                """
                ScoreRecordEntity[
                    Score ID = %d, User ID = %d, Submit Date = %s,
                    Correct Count = %d, Error Count = %d, No Answer Count = %d
                ]
                """,
                scoreId, userId, submitDate,
                correctCount, errorCount, noAnswerCount
        );
    }
}