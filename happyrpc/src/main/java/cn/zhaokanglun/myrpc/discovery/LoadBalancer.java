package cn.zhaokanglun.myrpc.discovery;

import java.net.InetSocketAddress;

public interface LoadBalancer {
    InetSocketAddress getOneAddress(String serviceName);
}
