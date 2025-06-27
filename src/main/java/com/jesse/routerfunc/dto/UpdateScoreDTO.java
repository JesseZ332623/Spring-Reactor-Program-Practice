package com.jesse.routerfunc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UpdateScoreDTO
{
    // 成绩记录 ID
    private Integer scoreId;

    // 正确数
    private Integer correctCount  = 0;

    // 错误数
    private Integer errorCount    = 0;

    // 未答数
    private Integer noAnswerCount = 0;
}
