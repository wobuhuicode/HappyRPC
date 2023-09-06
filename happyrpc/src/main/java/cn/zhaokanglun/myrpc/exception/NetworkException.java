package cn.zhaokanglun.myrpc.exception;

public class NetworkException extends MyRpcException {
    public NetworkException(Exception e) {
        super(e);
    }

    public NetworkException(String message) {
        super(message);
    }
}
