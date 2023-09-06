package cn.zhaokanglun.myrpc.spring.boot.autoconfigure.demoservice;

public class AddImpl implements Add {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
