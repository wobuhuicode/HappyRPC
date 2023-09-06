package cn.zhaokanglun.myrpc.bootstrap;

public interface ReferenceFactory {
    <T> T getReference(Class<T> clz);
}
