package cn.zhaokanglun.myrpc.network;

import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.exception.NetworkException;
import cn.zhaokanglun.myrpc.exception.ParameterException;
import cn.zhaokanglun.myrpc.network.handler.RpcDecoder;
import cn.zhaokanglun.myrpc.network.handler.RpcEncoder;
import cn.zhaokanglun.myrpc.network.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import static cn.zhaokanglun.myrpc.cache.Storage.SERVICE_MAP;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final ServerBootstrap serverBootstrap;

    public Server() {
        serverBootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        // duplex
                        channel.pipeline().addLast(new IdleStateHandler(10, 0, 0));

                        // Outbound
                        channel.pipeline().addLast(new RpcEncoder());

                        // Inbound
                        channel.pipeline().addLast(new RpcDecoder());
                        channel.pipeline().addLast(new ServerHandler());
                    }
                });
        logger.info("server initializing");
    }

    public void bind(InetSocketAddress address) throws NetworkException {
        try {
            serverBootstrap.bind(address).get();
            logger.info("server bind to: " + address);
        } catch (InterruptedException | ExecutionException e) {
            throw new NetworkException("server bind error");
        }
    }

    public void simpleRegister(Object serviceImpl) throws MyRpcException {
        if (serviceImpl.getClass().getInterfaces().length != 1) {
            throw new ParameterException("serviceImpl must implement 1 interface");
        }

        SERVICE_MAP.put(serviceImpl.getClass().getInterfaces()[0].getSimpleName(), serviceImpl);
        logger.info("simple register service: " + serviceImpl.getClass().getInterfaces()[0].getSimpleName());
    }
}
