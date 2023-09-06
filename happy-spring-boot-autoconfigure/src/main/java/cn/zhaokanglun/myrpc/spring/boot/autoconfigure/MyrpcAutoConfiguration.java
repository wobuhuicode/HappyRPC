package cn.zhaokanglun.myrpc.spring.boot.autoconfigure;

import cn.zhaokanglun.myrpc.bootstrap.DiscoveryBootstrap;
import cn.zhaokanglun.myrpc.bootstrap.ReferenceFactory;
import cn.zhaokanglun.myrpc.bootstrap.SimpleBootstrap;
import cn.zhaokanglun.myrpc.discovery.Registry;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@EnableConfigurationProperties(MyrpcProperties.class)
public class MyrpcAutoConfiguration implements InitializingBean {
    private MyrpcProperties myrpcProperties;

    @Autowired
    public void setMyrpcProperties(MyrpcProperties myrpcProperties) {
        this.myrpcProperties = myrpcProperties;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println(myrpcProperties);
    }

    @Bean
    public ReferenceFactory myrpcReference() throws MyRpcException {
        ReferenceFactory ret;
        if (myrpcProperties.getMode() == 0) {
            String[] hostAndPort = myrpcProperties.getServerAddressForSimpleMode().split(":");
            String hostname = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            ret = new SimpleBootstrap(new InetSocketAddress(hostname, port), myrpcProperties.getPort());
        } else {
            ret = new DiscoveryBootstrap(myrpcProperties.getDiscoveryAddress(), myrpcProperties.getPort());
        }
        return ret;
    }

    @Bean
    public Registry myrpcRegistry(ReferenceFactory referenceFactory) {
        if (myrpcProperties.getMode() == 0) {
            return null;
        }
        return ((DiscoveryBootstrap) referenceFactory).getRegistry();
    }

    @Bean
    public MyrpcPostProcessor myrpcPostProcessor() {
        return new MyrpcPostProcessor();
    }
}
