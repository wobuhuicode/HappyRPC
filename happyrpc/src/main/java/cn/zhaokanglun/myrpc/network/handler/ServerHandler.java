package cn.zhaokanglun.myrpc.network.handler;

import cn.zhaokanglun.myrpc.exception.TargetException;
import cn.zhaokanglun.myrpc.protocol.RpcInvocation;
import cn.zhaokanglun.myrpc.protocol.RpcProtocol;
import cn.zhaokanglun.myrpc.protocol.RpcResponse;
import cn.zhaokanglun.myrpc.serialization.Serializer;
import cn.zhaokanglun.myrpc.serialization.fastjson2.FastJson2Serializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static cn.zhaokanglun.myrpc.cache.Storage.SERVICE_MAP;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private final Serializer serializer = FastJson2Serializer.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcProtocol protocol = (RpcProtocol) msg;
        if (protocol.getStart() == RpcProtocol.HEARTBEAT_HEAD || protocol.getStart() != RpcProtocol.INVOCATION_HEAD) {
            return;
        }

        // 反序列化
        logger.debug("server receive protocol: " + protocol);
        RpcInvocation invocation = serializer.deserialize(protocol.getContent(), RpcInvocation.class);
        logger.debug("server receive invocation: " + invocation);
        Object service = SERVICE_MAP.get(invocation.getServiceName());
        Class<?>[] argsType = new Class[invocation.getArgs().length];
        Object ret = null;
        Throwable targetException = null;

        // 读取参数类型
        for (int i = 0; i < invocation.getArgs().length; i++) {
            argsType[i] = invocation.getArgs()[i].getClass();
        }

        // 匹配目标方法
        try {
            Method method = service.getClass().getMethod(invocation.getMethodName(), argsType);
            // 调用目标方法
            ret = method.invoke(service, invocation.getArgs());
        } catch (NullPointerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            targetException = new TargetException(e.getMessage());
        }

        // 构造返回体
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setUuid(invocation.getUuid());
        rpcResponse.setRet(ret);
        rpcResponse.setException(targetException);

        // 返回响应
        RpcProtocol responseProtocol = new RpcProtocol(RpcProtocol.RESPONSE_HEAD, serializer.serialize(rpcResponse));
        logger.debug("server send response: " + rpcResponse);
        ctx.writeAndFlush(responseProtocol);
    }

    // 接受心跳，由于服务端状态由注册中心监控，因此服务端不需要发送心跳
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            ctx.channel().close();
        }
    }
}
