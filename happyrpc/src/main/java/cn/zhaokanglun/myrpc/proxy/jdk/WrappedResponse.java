package cn.zhaokanglun.myrpc.proxy.jdk;

import cn.zhaokanglun.myrpc.protocol.RpcResponse;

import java.util.concurrent.CountDownLatch;

public class WrappedResponse {
    private RpcResponse response;
    private final CountDownLatch countDownLatch;

    public WrappedResponse(RpcResponse response, CountDownLatch countDownLatch) {
        this.response = response;
        this.countDownLatch = countDownLatch;
    }

    public RpcResponse getResponse() {
        return response;
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

}
