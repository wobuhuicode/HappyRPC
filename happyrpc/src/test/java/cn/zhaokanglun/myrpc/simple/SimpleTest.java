package cn.zhaokanglun.myrpc.simple;

import cn.zhaokanglun.myrpc.bootstrap.SimpleBootstrap;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.service.Add;
import cn.zhaokanglun.myrpc.service.impl.AddImpl;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

public class SimpleTest {
    @Test
    void simpleTest() {
        try {
            SimpleBootstrap simpleBootstrap = new SimpleBootstrap(new InetSocketAddress(6666), 6666);
            simpleBootstrap.simpleRegister(new AddImpl());
            Add addReference = simpleBootstrap.getReference(Add.class);
            assert addReference.add(10, 6) == 16;
        } catch (MyRpcException e) {
            throw new RuntimeException(e);
        }
    }
}
