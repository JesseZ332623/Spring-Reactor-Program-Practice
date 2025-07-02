package com.jesse.routerfunc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(
        shape   = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime submitDate = LocalDateTime.now();

    // 正确数
    private Integer correctCount = 0;

    // 错误数
    private Integer errorCount = 0;

    // 未答数
    private Integer noAnswerCount = 0;

//    @Override
//    public String toString()
//    {
//        return format(
//            "ScoreQueryDTO(score = %d, userId = %d, userName = %s," +
//            "submitDate = %s, correctCount = %d, errorCount = %d, noAnswerCount = %d)",
//            scoreId, userId, userName,
//            submitDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
//            correctCount, errorCount, noAnswerCount
//        );
//    }
}