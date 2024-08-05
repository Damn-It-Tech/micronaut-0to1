package com.cardbff.interceptor;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LoggingInterceptor implements MethodInterceptor<Object, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        String methodName = context.getMethodName();
        String className= context.getTarget().getClass().getSimpleName().split("\\$")[1];

        LOG.info("Starting method: {} ---#--- {}", className, methodName);

        long startTime = System.currentTimeMillis();
        Object result = context.proceed();
        long endTime = System.currentTimeMillis();

        LOG.info("Finished method: {} ---#--- {} (took {} ms)", className, methodName, (endTime - startTime));

        return result;
    }
}