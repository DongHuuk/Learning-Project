package com.providelearingsite.siteproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class AsyncExceptionIOHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("Async Uncaught Exception : {}", throwable.getMessage());
        log.error("error class name : {}", method.getDeclaringClass().getName() + ", {}" + method.getName());
    }
}
