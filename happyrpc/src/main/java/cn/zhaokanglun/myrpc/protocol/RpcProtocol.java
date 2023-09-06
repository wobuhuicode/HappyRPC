package cn.zhaokanglun.myrpc.protocol;

/**
 * 统一协议体为RPC传输的统一对象，是直接被序列化的对象。其内容可以是心跳、调用对象或返回对象。
 */
public class RpcProtocol {
    public static final short HEARTBEAT_HEAD = 97;
    public static final short INVOCATION_HEAD = 98;
    public static final short RESPONSE_HEAD = 99;
    private short start;
    private int length;
    private byte[] content;

    public RpcProtocol(short start, byte[] content) {
        this.start = start;
        this.length = content.length;
        this.content = content;
    }

    public short getStart() {
        return start;
    }

    public void setStart(short start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RpcProtocol{" +
                "start=" + start +
                ", length=" + length +
                '}';
    }
}
