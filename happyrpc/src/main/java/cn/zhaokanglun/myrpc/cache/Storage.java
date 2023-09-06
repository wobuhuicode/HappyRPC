package cn.zhaokanglun.myrpc.cache;

import cn.zhaokanglun.myrpc.proxy.jdk.WrappedResponse;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存存储
 */
public class Storage {
    /**
     * 响应映射，存储未返回的响应
     */
    public static final Map<UUID, WrappedResponse> RESPONSE_MAP = new ConcurrentHashMap<>();

    /**
     * CHANNEL_FUTURE映射，CHANNEL_FUTURE是Netty提供的CHANNEL包装
     */
    public static final Map<SocketAddress, ChannelFuture> CHANNEL_FUTURE_MAP = new ConcurrentHashMap<>();

    /**
     * 服务列表映射，存储从注册中心获取的服务地址列表
     */
    public static final Map<String, List<InetSocketAddress>> SERVICE_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 服务映射，存储已注册的实际服务提供对象
     */
    public static final Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();
}
