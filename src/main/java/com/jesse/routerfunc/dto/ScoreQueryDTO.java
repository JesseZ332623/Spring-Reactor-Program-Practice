package com.jesse.routerfunc.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "scoreId")
public class ScoreQueryDTO
{
    // 成绩记录 ID
    private Integer scoreId;

    // 用户 ID
    private @NonNull Integer userId;

    // 用户名
    private @NotNull String userName;

    // 成绩提交日期
    private @NonNull
    LocalDateTime submitDate = LocalDateTime.now();

    // 正确数
    private @NonNull
    Integer correctCount = 0;

    // 错误数
    private @NonNull
    Integer errorCount = 0;

    // 未答数
    private @NonNull
    Integer noAnswerCount = 0;
}
