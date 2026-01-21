package com.kama.server.ratelimit.impl;

import com.kama.server.ratelimit.RateLimit;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName TokenBucketRateLimitImpl
 * @Description 全局限流
 * 
 * 
 * @Version 1.0.0
 */

@Slf4j
public class TokenBucketRateLimitImpl implements RateLimit {

    // 令牌产生速率（单位：ms�?
    private final int rate;
    // 桶容�?
    private final int capacity;
    // 当前桶容�?
    private volatile int curCapacity;
    // 上次请求时间�?
    private volatile long lastTimestamp;

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        this.rate = rate;
        this.capacity = capacity;
        this.curCapacity = capacity;
        this.lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public boolean getToken() {
        // 优化：同步仅限于关键部分，减少锁竞争
        synchronized (this) {
            // 如果当前桶还有剩余，就直接返�?
            if (curCapacity > 0) {
                curCapacity--;
                return true;
            }

            long currentTimestamp = System.currentTimeMillis();
            // 如果距离上一次请求的时间大于 RATE 的时间间�?
            if (currentTimestamp - lastTimestamp >= rate) {
                // 计算这段时间内生成的令牌数量
                int generatedTokens = (int) ((currentTimestamp - lastTimestamp) / rate);
                if (generatedTokens > 1) {
                    // 只添加剩余令牌，确保不会超过桶的容量
                    curCapacity = Math.min(capacity, curCapacity + generatedTokens - 1);
                }
                // 更新时间�?
                lastTimestamp = currentTimestamp;
                return true;
            }
            return false;  // 如果无法获取令牌，返�?false
        }
    }
}
