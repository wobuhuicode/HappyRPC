package cn.zhaokanglun.myrpc.protocol;

import java.util.UUID;

public class RpcResponse {
    private Object ret;
    private Throwable exception;
    private UUID uuid;

    public Object getRet() {
        return ret;
    }

    public void setRet(Object ret) {
        this.ret = ret;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "ret=" + ret +
                ", exception=" + exception +
                ", uuid=" + uuid +
                '}';
    }
}
