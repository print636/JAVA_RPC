package com.kama.client.servicecenter.balance;


import java.util.List;

/**
 * @InterfaceName LoadBalance
 * @Description 负载均衡接口
 * 
 * @Version 1.0.0
 */

public interface LoadBalance {
    String balance(List<String> addressList);

    void addNode(String node);

    void delNode(String node);
}
