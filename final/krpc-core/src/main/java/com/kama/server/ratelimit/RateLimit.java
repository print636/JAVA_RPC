package com.kama.server.ratelimit;


/**
 * @InterfaceName RateLimit
 * @Description 限流接口
 * 
 * 
 * @Version 1.0.0
 */

public interface RateLimit {
    //获取访问许可
    boolean getToken();
}
