package com.kama.client.servicecenter;


import common.message.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @InterfaceName ServiceCenter
 * @Description 服务中心接口
 * 
 * 
 * @Version 1.0.0
 */

public interface ServiceCenter {
    //  查询：根据服务名查找地址
    InetSocketAddress serviceDiscovery(RpcRequest request);

    //判断是否可重�?
    boolean checkRetry(InetSocketAddress serviceAddress, String methodSignature);

    //关闭客户�?
    void close();
}
