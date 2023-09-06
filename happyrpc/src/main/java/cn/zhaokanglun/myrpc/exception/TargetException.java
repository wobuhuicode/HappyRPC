package cn.zhaokanglun.myrpc.exception;

public class TargetException extends MyRpcException {
    public TargetException(Exception e) {
        super(e);
    }

    public TargetException(String message) {
        super(message);
    }

}
