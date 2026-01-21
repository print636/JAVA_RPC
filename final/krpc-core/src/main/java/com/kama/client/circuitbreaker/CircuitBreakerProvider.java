package com.kama.client.circuitbreaker;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CircuitBreakerState
 * @Description 提供熔断�?
 * 
 * 
 * @Version 1.0.0
 */
@Slf4j
public class CircuitBreakerProvider {
    // 使用线程安全�?ConcurrentHashMap
    private Map<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    public synchronized CircuitBreaker getCircuitBreaker(String serviceName) {
        // 使用 computeIfAbsent，避免手动同�?
        return circuitBreakerMap.computeIfAbsent(serviceName, key -> {
            log.info("服务 [{}] 不存在熔断器，创建新的熔断器实例", serviceName);
            // 创建并返回新熔断�?
            return new CircuitBreaker(1, 0.5, 10000);
        });
    }
}
