package com.jesse.routerfunc;

import io.netty.util.internal.ThreadLocalRandom;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RandomTimeGenerator
{
    /**
     * 给点时间范围，随机生成范围内的时间。
     *
     * @param begin 开始时间
     * @param end   结束时间
     *
     * @return 随机生成范围内的时间。
     */
    public static LocalDateTime
    randomBetween(LocalDateTime begin, LocalDateTime end)
    {
        long startEpoch = begin.toEpochSecond(ZoneOffset.UTC);
        long endEpoch   = end.toEpochSecond(ZoneOffset.UTC);

        return LocalDateTime.ofEpochSecond(
            ThreadLocalRandom.current().nextLong(startEpoch, endEpoch),
            0, ZoneOffset.UTC
        );
    }
}
