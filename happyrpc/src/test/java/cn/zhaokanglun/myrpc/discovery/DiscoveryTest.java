package cn.zhaokanglun.myrpc.discovery;

import cn.zhaokanglun.myrpc.bootstrap.DiscoveryBootstrap;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.service.Add;
import cn.zhaokanglun.myrpc.service.impl.AddImpl;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class DiscoveryTest {
    @Test
    public void discoveryTest() throws InterruptedException {
        try {
            DiscoveryBootstrap bootstrap = new DiscoveryBootstrap("10.112.91.126:8848", 9191);
            Registry registry = bootstrap.getRegistry();
            Add addReference = bootstrap.getReference(Add.class);
            registry.register(new AddImpl());
            TimeUnit.SECONDS.sleep(1);
            registry.subscribe(Add.class.getSimpleName());
            assert addReference.add(6, 10) == 16;
            System.out.println(addReference.add(6, 10));
        } catch (MyRpcException e) {
            throw new RuntimeException(e);
        }
    }
}
