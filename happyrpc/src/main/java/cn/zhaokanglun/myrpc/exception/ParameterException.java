package cn.zhaokanglun.myrpc.exception;

public class ParameterException extends MyRpcException {
    public ParameterException(Exception e) {
        super(e);
    }

    public ParameterException(String message) {
        super(message);
    }
}
