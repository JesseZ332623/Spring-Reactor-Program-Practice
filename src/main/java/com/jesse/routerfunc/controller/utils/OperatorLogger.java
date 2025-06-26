package com.jesse.routerfunc.controller.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OperatorLogger
{
    public void
    logStartedOperator(String message) {
        log.info(message);
    }

    public void
    logSuccessOperator(String message) {
        log.info(message);
    }

    public void
    logErrorOperator(String message, Throwable error) {
        log.error(message, error);
    }
}
