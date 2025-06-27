package com.jesse.routerfunc.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "scoreId")
public class ScoreQueryDTO
{
    // 成绩记录 ID
    private Integer scoreId;

    // 用户 ID
    private Long userId;

    // 用户名
    private String userName;

    // 成绩提交日期
    private LocalDateTime submitDate = LocalDateTime.now();

    // 正确数
    private Integer correctCount = 0;

    // 错误数
    private Integer errorCount = 0;

    // 未答数
    private Integer noAnswerCount = 0;
}
