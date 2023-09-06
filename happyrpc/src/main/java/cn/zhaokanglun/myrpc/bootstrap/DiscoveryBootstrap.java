package cn.zhaokanglun.myrpc.bootstrap;

import cn.zhaokanglun.myrpc.discovery.Registry;
import cn.zhaokanglun.myrpc.discovery.nacos.NacosRegistry;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.network.Client;
import cn.zhaokanglun.myrpc.network.Server;
import cn.zhaokanglun.myrpc.proxy.ProxyFactory;
import cn.zhaokanglun.myrpc.proxy.jdk.JdkProxyFactory;

import java.net.InetSocketAddress;

public class DiscoveryBootstrap implements ReferenceFactory {
    private final Registry registry;
    private final ProxyFactory proxyFactory;

    public DiscoveryBootstrap(String discoveryAddress, int port) throws MyRpcException {
        Server server = new Server();
        Client client = new Client();
        InetSocketAddress local = new InetSocketAddress(port);
        client.setMode(1);
        registry = new NacosRegistry(discoveryAddress, local, client.getManager());
        proxyFactory = new JdkProxyFactory(client);
        server.bind(local);
    }

    public Registry getRegistry() {
        return registry;
    }

    @Override
    public <T> T getReference(Class<T> clz) {
        return proxyFactory.getProxy(clz);
    }
}
