package cn.zhaokanglun.myrpc.serialization;


public interface Serializer {
    byte[] serialize(Object obj);
    <T> T deserialize(byte[] byteBuf, Class<T> clz);
}
