package cn.zhaokanglun.myrpc.discovery.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class NacosTest {
    @Test
    public void registerTest() {
        try {
            NamingService naming = NamingFactory.createNamingService("10.112.91.126:8848");
            naming.registerInstance("张紫涵666", "ip", 8888);
            System.in.read();
        } catch (NacosException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
