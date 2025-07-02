package com.jesse.routerfunc.controller.exception;

/** 偏移量越界时所抛的异常。 */
public class OffsetOutOfRange extends RuntimeException
{
    public OffsetOutOfRange(String message) {
        super(message);
    }
}
