package cn.zhaokanglun.myrpc.serialization.fastjson2;


import cn.zhaokanglun.myrpc.serialization.Serializer;
import com.alibaba.fastjson2.JSON;

public class FastJson2Serializer implements Serializer {
    private volatile static Serializer instance;

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        return JSON.parseObject(data, clz);
    }

    public static Serializer getInstance() {
        if (instance == null) {
            synchronized (FastJson2Serializer.class) {
                if (instance == null) {
                    instance = new FastJson2Serializer();
                }
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return "FastJson2";
    }
}
