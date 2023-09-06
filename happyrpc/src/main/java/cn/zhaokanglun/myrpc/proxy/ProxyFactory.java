package cn.zhaokanglun.myrpc.proxy;

public interface ProxyFactory {
    <T> T getProxy(Class<T> clz);
}
