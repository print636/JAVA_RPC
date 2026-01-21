package com.kama.client.netty;


import common.serializer.mycoder.MyDecoder;
import common.serializer.mycoder.MyEncoder;
import common.serializer.myserializer.Serializer;
import common.trace.TraceContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.MDC;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName NettyClientInitializer
 * @Description Netty Client Pipeline Configuration
 * @Version 1.0.0
 */
@Slf4j
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    public NettyClientInitializer(){}
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // Configure custom encoder and decoder
        try {
            // Use JDK serialization (code=0) to ensure serializer availability
            Serializer serializer = Serializer.getSerializerByCode(0);
            pipeline.addLast(new MyEncoder(serializer));
            pipeline.addLast(new MyDecoder());
            pipeline.addLast(new NettyClientHandler());
            pipeline.addLast(new MDCChannelHandler());
            // Client monitors write events, sends heartbeat if no data sent for 8 seconds
            pipeline.addLast(new IdleStateHandler(0, 8, 0, TimeUnit.SECONDS));
            pipeline.addLast(new HeartbeatHandler());
            log.info("Netty client pipeline initialized with serializer type: {}", serializer.getType());
        } catch (Exception e) {
            log.error("Error initializing Netty client pipeline", e);
            throw e;  // Re-throw exception to ensure pipeline initialization failure is handled
        }
    }
}
