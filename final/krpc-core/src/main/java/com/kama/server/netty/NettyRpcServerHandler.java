package com.kama.server.netty;


import com.kama.server.provider.ServiceProvider;
import com.kama.server.ratelimit.RateLimit;
import com.kama.trace.interceptor.ServerTraceInterceptor;
import common.message.RequestType;
import common.message.RpcRequest;
import common.message.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName NettyRpcServerHandler
 * @Description 服务端处理器
 * 
 * 
 * @Version 1.0.0
 */
@AllArgsConstructor  // 使用 Lombok 自动生成构造器
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final ServiceProvider serviceProvider;  // 确保通过构造器注入 ServiceProvider

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        if (request == null) {
            log.error("接收到非法请求，RpcRequest 为空");
            return;
        }
        if(request.getType() == RequestType.HEARTBEAT){
            log.info("接收到来自客户端的心跳包");
            return;
        }
        if(request.getType() == RequestType.NORMAL) {
            //trace记录
            ServerTraceInterceptor.beforeHandle();

            RpcResponse response = getResponse(request);
            //ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

            //trace上报
            ServerTraceInterceptor.afterHandle(request.getMethodName());

            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("处理请求时发生异�? ", cause);
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest) {
        //得到服务�?
        String interfaceName = rpcRequest.getInterfaceName();

        //接口限流降级
        RateLimit rateLimit = serviceProvider.getRateLimitProvider().getRateLimit(interfaceName);
        if (!rateLimit.getToken()) {
            //如果获取令牌失败，进行限流降级，快速返回结�?
            log.warn("服务限流，接�? {}", interfaceName);
            return RpcResponse.fail("服务限流，接�?" + interfaceName + " 当前无法处理请求。请稍后再试�?);
        }

        //得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        //反射调用方法
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object invoke = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.sussess(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("方法执行错误，接�? {}, 方法: {}", interfaceName, rpcRequest.getMethodName(), e);
            return RpcResponse.fail("方法执行错误");
        }
    }
}
