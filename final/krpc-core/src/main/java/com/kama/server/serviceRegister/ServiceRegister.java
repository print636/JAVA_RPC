package com.kama.server.serviceRegister;


import java.net.InetSocketAddress;

/**
 * @InterfaceName ServiceRegister
 * @Description 服务注册接口
 * 
 * 
 * @Version 1.0.0
 */

public interface ServiceRegister {
    void register(Class<?> clazz, InetSocketAddress serviceAddress);
}
