package cn.zhaokanglun.myrpc.proxy.jdk;

import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.network.Client;
import cn.zhaokanglun.myrpc.protocol.RpcInvocation;
import cn.zhaokanglun.myrpc.protocol.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static cn.zhaokanglun.myrpc.cache.Storage.RESPONSE_MAP;

public class RpcInvocationHandler implements InvocationHandler {
    private volatile static RpcInvocationHandler instance;
    private final Client client;

    private RpcInvocationHandler(Client client) {
        this.client = client;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws RuntimeException {
        RpcInvocation invocation = new RpcInvocation();
        RpcResponse response = new RpcResponse();
        UUID uuid = UUID.randomUUID();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        response.setUuid(uuid);
        WrappedResponse wrappedResponse = new WrappedResponse(response, countDownLatch);
        RESPONSE_MAP.put(uuid, wrappedResponse);

        invocation.setServiceName(method.getDeclaringClass().getSimpleName());
        invocation.setMethodName(method.getName());
        invocation.setArgs(args);
        invocation.setUuid(uuid);

        try {
            client.send(invocation);
            boolean success = countDownLatch.await(30, TimeUnit.SECONDS);
            if (!success) {
                throw new RuntimeException("rpc invoke timeout");
            }
            response = RESPONSE_MAP.get(uuid).getResponse();
        } catch (InterruptedException | MyRpcException e) {
            throw new RuntimeException(e);
        } finally {
            RESPONSE_MAP.remove(uuid);
        }

        if (response.getException() != null) {
            throw new RuntimeException(response.getException());
        }

        return response.getRet();
    }

    public static RpcInvocationHandler getInstance(Client client) {
        if (instance == null) {
            synchronized (RpcInvocation.class) {
                if (instance == null) {
                    instance = new RpcInvocationHandler(client);
                }
            }
        }
        return instance;
    }
}
