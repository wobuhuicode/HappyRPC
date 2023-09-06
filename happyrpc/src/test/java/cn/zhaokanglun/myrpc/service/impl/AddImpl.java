package cn.zhaokanglun.myrpc.service.impl;

import cn.zhaokanglun.myrpc.service.Add;

public class AddImpl implements Add {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }
}
