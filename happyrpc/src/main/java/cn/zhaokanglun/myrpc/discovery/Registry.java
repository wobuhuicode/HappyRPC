package cn.zhaokanglun.myrpc.discovery;

import cn.zhaokanglun.myrpc.exception.MyRpcException;

import java.net.InetSocketAddress;
import java.util.List;

public interface Registry {
    void register(Object serviceObj) throws MyRpcException;
    void deregister(Object serviceObj) throws MyRpcException;

    void subscribe(String serviceName) throws MyRpcException;

    void unsubscribe(String serviceName) throws MyRpcException;

    List<InetSocketAddress> find(String serviceName) throws MyRpcException;
}
