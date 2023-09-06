package cn.zhaokanglun.myrpc.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

import static cn.zhaokanglun.myrpc.cache.Storage.CHANNEL_FUTURE_MAP;

public class ClientConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionManager.class);
    private final Bootstrap bootstrap;

    public ClientConnectionManager(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connectToList(List<InetSocketAddress> addresses) {
        for (InetSocketAddress address : addresses) {
            CHANNEL_FUTURE_MAP.put(address, bootstrap.connect(address));
            logger.debug("client connected to " + address);
        }
    }

    public void connect(InetSocketAddress address) {
        CHANNEL_FUTURE_MAP.put(address, bootstrap.connect(address));
        logger.info("client connected to " + address);
    }

    public void disconnect(InetSocketAddress address) {
        ChannelFuture channelFuture = CHANNEL_FUTURE_MAP.remove(address);
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
    }
}
