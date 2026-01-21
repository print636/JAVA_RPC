package com.kama.client.circuitbreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName CircuitBreaker
 * @Description 熔断器的状�?
 * 
 * 
 * @Version 1.0.0
 */
@Slf4j
public class CircuitBreaker {
    //当前状�?
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private AtomicInteger failureCount = new AtomicInteger(0);
    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger requestCount = new AtomicInteger(0);
    //失败次数阈�?
    private final int failureThreshold;
    //半开�?》关闭状态的成功次数比例
    private final double halfOpenSuccessRate;
    //恢复时间
    private final long retryTimePeriod;
    //上一次失败时�?
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.retryTimePeriod = retryTimePeriod;
    }

    //查看当前熔断器是否允许请求通过
    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        log.info("熔断前检�? 当前失败次数：{}", failureCount);
        switch (state) {
            case OPEN:
                if (currentTime - lastFailureTime > retryTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    log.info("熔断已解除，进入半开启状态，允许请求通过");
                    return true;
                }
                log.warn("熔断生效中，拒绝请求�?);
                return false;
            case HALF_OPEN:
                requestCount.incrementAndGet();
                log.info("当前为半开启状态，计数请求");
                return true;
            case CLOSED:
            default:
                log.info("当前为正常状态，允许请求通过");
                return true;
        }
    }

    //记录成功
    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            if (successCount.get() >= halfOpenSuccessRate * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
                log.info("成功次数已达到阈值，熔断器切换至关闭状�?);
            }
        } else {
            resetCounts();
            log.info("熔断器处于关闭状态，重置计数�?);
        }
    }

    //记录失败
    public synchronized void recordFailure() {
        failureCount.incrementAndGet();
        log.error("记录失败，当前失败次数：{}", failureCount);
        lastFailureTime = System.currentTimeMillis();

        if (state == CircuitBreakerState.HALF_OPEN) {
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
            log.warn("半开启状态下发生失败，熔断器切换至开启状�?);
        } else if (failureCount.get() >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
            log.error("失败次数已超过阈值，熔断器切换至开启状�?);
        }
    }

    //重置次数
    private void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }

    public CircuitBreakerState getState() {
        return state;
    }
}

enum CircuitBreakerState {
    //关闭，开启，半开�?
    CLOSED, OPEN, HALF_OPEN
}
