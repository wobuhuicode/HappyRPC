package cn.zhaokanglun.myrpc.network;

import cn.zhaokanglun.myrpc.discovery.LoadBalancer;
import cn.zhaokanglun.myrpc.discovery.balance.RandomLoadBalancer;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.exception.NetworkException;
import cn.zhaokanglun.myrpc.exception.ParameterException;
import cn.zhaokanglun.myrpc.network.handler.ClientHandler;
import cn.zhaokanglun.myrpc.network.handler.RpcDecoder;
import cn.zhaokanglun.myrpc.network.handler.RpcEncoder;
import cn.zhaokanglun.myrpc.protocol.RpcInvocation;
import cn.zhaokanglun.myrpc.protocol.RpcProtocol;
import cn.zhaokanglun.myrpc.serialization.Serializer;
import cn.zhaokanglun.myrpc.serialization.fastjson2.FastJson2Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.zhaokanglun.myrpc.cache.Storage.CHANNEL_FUTURE_MAP;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final Serializer serializer;
    private final LoadBalancer loadBalancer;

    private final ClientConnectionManager manager;

    // 0-simple 1-discovery
    private int mode = 0;

    private InetSocketAddress simpleModeServerAddress;

    private final ThreadPoolExecutor sendExecutor = new ThreadPoolExecutor(1, 1, 1,
                    TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    public Client() {
        logger.info("client initializing");
        serializer = FastJson2Serializer.getInstance();
        logger.info("using serializer of " + serializer);
        loadBalancer = RandomLoadBalancer.getInstance();
        logger.info("using load balancer of " + loadBalancer);
        // OutBound
        // duplex
        // InBound
        Bootstrap bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup())
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        // OutBound
                        channel.pipeline().addLast(new RpcEncoder());

                        // duplex
                        channel.pipeline().addLast(new IdleStateHandler(0, 3, 0));

                        // InBound
                        channel.pipeline().addLast(new RpcDecoder());
                        channel.pipeline().addLast(new ClientHandler());
                    }
                });
        manager = new ClientConnectionManager(bootstrap);
    }

    public void setMode(int mode) {
        this.mode = mode;
        if (mode == 0) {
            logger.info("set client mode to simple");
        } else {
            logger.info("set client mode to discovery");
        }
    }

    public void send(RpcInvocation invocation) throws MyRpcException {
        InetSocketAddress address;
        if (mode == 0) {
            address = this.simpleModeServerAddress;
        } else {
            address = loadBalancer.getOneAddress(invocation.getServiceName());
        }

        if (address == null) {
            throw new ParameterException("there is no provider for service: " + invocation.getServiceName());
        }

        ChannelFuture channelFuture = CHANNEL_FUTURE_MAP.get(address);
        if (channelFuture == null) {
            throw new ParameterException("channel future for address: " + address + " do not exist");
        }

        try {
            channelFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new NetworkException("channel future for address: " + address + " connect failed");
        }

        RpcProtocol protocol = new RpcProtocol(RpcProtocol.INVOCATION_HEAD, serializer.serialize(invocation));
        channelFuture.channel().writeAndFlush(protocol);
        logger.debug("send rpc invocation " + invocation + " to " + address);
    }

    public void setSimpleModeServerAddress(InetSocketAddress address) {
        this.simpleModeServerAddress = address;
        logger.info("set address for simple mode to " + address);
    }

    public ClientConnectionManager getManager() {
        return manager;
    }
}
