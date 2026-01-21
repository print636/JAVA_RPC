package com.kama.client.rpcclient.impl;

import com.kama.client.netty.MDCChannelHandler;
import com.kama.client.netty.NettyClientInitializer;
import com.kama.client.rpcclient.RpcClient;
import common.message.RpcRequest;
import common.message.RpcResponse;
import common.trace.TraceContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @ClassName NettyRpcClient
 * @Description Netty客户�?
 * 
 * 
 * @Version 1.0.0
 */
@Slf4j
public class NettyRpcClient implements RpcClient {

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    private final InetSocketAddress address;

    public NettyRpcClient(InetSocketAddress serviceAddress) {
        this.address = serviceAddress;
    }

    //netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        Map<String,String> mdcContextMap=TraceContext.getCopy();
        //从注册中心获取host,post
        if (address == null) {
            log.error("服务发现失败，返回的地址�?null");
            return RpcResponse.fail("服务发现失败，地址�?null");
        }
        String host = address.getHostName();
        int port = address.getPort();
        try {
            // 连接到远程服�?
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            // 将当前Trace上下文保存到Channel属�?
            channel.attr(MDCChannelHandler.TRACE_CONTEXT_KEY).set(mdcContextMap);

            // 发送数�?
            channel.writeAndFlush(request);
            //sync()堵塞获取结果
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题�?
            // 当前场景下选择堵塞获取结果
            // 其它场景也可以选择添加监听器的方式来异步获取结�?channelFuture.addListener...
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();
            if (response == null) {
                log.error("服务响应为空，可能是请求失败或超�?);
                return RpcResponse.fail("服务响应为空");
            }

            log.info("收到响应: {}", response);
            return response;
        } catch (InterruptedException e) {
            log.error("请求被中断，发送请求失�? {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("发送请求时发生异常: {}", e.getMessage(), e);
        } finally {
            //
        }
        return RpcResponse.fail("请求失败");
    }

    // 优雅关闭 Netty 资源
    public void close() {
        try {
            if (eventLoopGroup != null) {
                eventLoopGroup.shutdownGracefully().sync();
            }
        } catch (InterruptedException e) {
            log.error("关闭 Netty 资源时发生异�? {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
