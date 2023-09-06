package cn.zhaokanglun.myrpc.spring.boot.autoconfigure;

import cn.zhaokanglun.myrpc.bootstrap.ReferenceFactory;
import cn.zhaokanglun.myrpc.bootstrap.SimpleBootstrap;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.spring.boot.autoconfigure.demoservice.Add;
import cn.zhaokanglun.myrpc.spring.boot.autoconfigure.demoservice.AddImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class Test implements InitializingBean {
    @MyrpcReference
    private Add add;

    private ReferenceFactory referenceFactory;

    @Autowired
    public void setReference(ReferenceFactory referenceFactory) {
        this.referenceFactory = referenceFactory;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println(new InetSocketAddress("0.0.0.0", 9199));
        System.out.println(new InetSocketAddress(9199));
        try {
            ((SimpleBootstrap) referenceFactory).simpleRegister(new AddImpl());
        } catch (MyRpcException e) {
            throw new RuntimeException(e);
        }
        System.out.println(add.add(10, 6));
    }
}
