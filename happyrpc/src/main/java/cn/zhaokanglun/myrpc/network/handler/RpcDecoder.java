package cn.zhaokanglun.myrpc.network.handler;

import cn.zhaokanglun.myrpc.protocol.RpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        byteBuf.markReaderIndex();
        short head = byteBuf.readShort();
        if (head != RpcProtocol.INVOCATION_HEAD &&
                head != RpcProtocol.RESPONSE_HEAD &&
                head != RpcProtocol.HEARTBEAT_HEAD) {
            return;
        }

        int length = byteBuf.readInt();
        if (byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] contentByteArray = new byte[length];
        byteBuf.readBytes(contentByteArray);
        RpcProtocol protocol = new RpcProtocol(head, contentByteArray);
        list.add(protocol);
        logger.debug("decoded protocol: " + protocol);
    }

}
