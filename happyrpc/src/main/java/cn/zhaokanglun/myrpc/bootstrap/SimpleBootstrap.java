package cn.zhaokanglun.myrpc.bootstrap;

import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.network.Client;
import cn.zhaokanglun.myrpc.network.ClientConnectionManager;
import cn.zhaokanglun.myrpc.network.Server;
import cn.zhaokanglun.myrpc.proxy.ProxyFactory;
import cn.zhaokanglun.myrpc.proxy.jdk.JdkProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class SimpleBootstrap implements ReferenceFactory {
    Logger logger = LoggerFactory.getLogger(SimpleBootstrap.class);
    private final ProxyFactory proxyFactory;
    private final Server server;

    public SimpleBootstrap(InetSocketAddress serverAddress, int port) throws MyRpcException {
        logger.info("rpc initialize");
        Client client = new Client();
        proxyFactory = new JdkProxyFactory(client);
        ClientConnectionManager manager = client.getManager();
        server = new Server();
        client.setMode(0);

        logger.info("rpc booting");
        server.bind(new InetSocketAddress(port));
        client.setSimpleModeServerAddress(serverAddress);
        manager.connect(serverAddress);
    }

    @Override
    public <T> T getReference(Class<T> clz) {
        return proxyFactory.getProxy(clz);
    }

    public void simpleRegister(Object serviceImpl) throws MyRpcException {
        server.simpleRegister(serviceImpl);
    }
}
