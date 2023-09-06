package cn.zhaokanglun.myrpc.network.handler;

import cn.zhaokanglun.myrpc.protocol.RpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcEncoder extends MessageToByteEncoder<RpcProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol protocol, ByteBuf byteBuf) {
        byteBuf.writeShort(protocol.getStart());
        byteBuf.writeInt(protocol.getLength());
        byteBuf.writeBytes(protocol.getContent());
        logger.debug("encoded protocol: " + protocol);
    }
}
