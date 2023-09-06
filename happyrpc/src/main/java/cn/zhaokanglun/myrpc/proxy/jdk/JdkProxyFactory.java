package cn.zhaokanglun.myrpc.proxy.jdk;

import cn.zhaokanglun.myrpc.network.Client;
import cn.zhaokanglun.myrpc.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

public class JdkProxyFactory implements ProxyFactory {
    private final Client client;

    public JdkProxyFactory(Client client) {
        this.client = client;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clz) {
        if (!clz.isInterface()) {
            throw new RuntimeException("service must be interface");
        }
        return (T) Proxy.newProxyInstance(clz.getClassLoader(),
                new Class[]{clz}, RpcInvocationHandler.getInstance(client));
    }
}
