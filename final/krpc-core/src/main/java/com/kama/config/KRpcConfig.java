package com.kama.config;

import com.kama.client.servicecenter.balance.impl.ConsistencyHashBalance;
import com.kama.server.serviceRegister.impl.ZKServiceRegister;
import common.serializer.myserializer.Serializer;
import lombok.*;

/**
 * @ClassName KRpcConfig
 * @Description 配置文件
 * 
 * 
 * @Version 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class KRpcConfig {
    //名称
    private String name = "krpc";
    //端口
    private Integer port = 9999;
    //主机�?
    private String host = "localhost";
    //版本�?
    private String version = "1.0.0";
    //注册中心
    private String registry = new ZKServiceRegister().toString();
    //序列化器
    private String serializer = Serializer.getSerializerByCode(3).toString();
    //负载均衡
    private String loadBalance = new ConsistencyHashBalance().toString();

}
