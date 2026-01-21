package com.kama.server.server;


/**
 * @InterfaceName RpcServer
 * @Description 服务端接�?
 * 
 * 
 * @Version 1.0.0
 */

public interface RpcServer {
    void start(int port);

    void stop();
}
