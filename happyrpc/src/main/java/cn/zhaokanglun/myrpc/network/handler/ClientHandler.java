package cn.zhaokanglun.myrpc.network.handler;

import cn.zhaokanglun.myrpc.protocol.RpcProtocol;
import cn.zhaokanglun.myrpc.protocol.RpcResponse;
import cn.zhaokanglun.myrpc.proxy.jdk.WrappedResponse;
import cn.zhaokanglun.myrpc.serialization.Serializer;
import cn.zhaokanglun.myrpc.serialization.fastjson2.FastJson2Serializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import static cn.zhaokanglun.myrpc.cache.Storage.RESPONSE_MAP;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Serializer serializer = FastJson2Serializer.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcProtocol protocol = (RpcProtocol) msg;
        if (protocol.getStart() != RpcProtocol.RESPONSE_HEAD) {
            return;
        }

        // 写入返回体
        logger.debug("client receive protocol: " + protocol);
        RpcResponse response = serializer.deserialize(protocol.getContent(), RpcResponse.class);
        logger.debug("client receive response: " + response);
        WrappedResponse wrappedResponse = RESPONSE_MAP.get(response.getUuid());
        wrappedResponse.setResponse(response);
        // 唤起调用线程
        wrappedResponse.getCountDownLatch().countDown();
    }

    // 负责向服务端发送心跳
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
            RpcProtocol heartbeatProtocol = new RpcProtocol(RpcProtocol.HEARTBEAT_HEAD, new byte[]{});
            ctx.writeAndFlush(heartbeatProtocol);
            logger.debug("client send heartbeat to " + ctx.channel().remoteAddress());
        }
    }
}
