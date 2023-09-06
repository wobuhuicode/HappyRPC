package cn.zhaokanglun.myrpc.exception;

public class MyRpcException extends Throwable {
    public MyRpcException(Exception e) {
        super(e);
    }

    public MyRpcException(String message) {
        super(message);
    }
}
