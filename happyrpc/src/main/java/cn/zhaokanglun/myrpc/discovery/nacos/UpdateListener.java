package cn.zhaokanglun.myrpc.discovery.nacos;

import cn.zhaokanglun.myrpc.network.ClientConnectionManager;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.zhaokanglun.myrpc.cache.Storage.SERVICE_LIST_MAP;

public class UpdateListener implements EventListener {
    private final ClientConnectionManager manager;

    UpdateListener(ClientConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof NamingEvent namingEvent) {
            String serviceName = namingEvent.getServiceName();
            Set<InetSocketAddress> addressSet = new HashSet<>();
            // 新连接Set
            for (Instance instance : namingEvent.getInstances()) {
                addressSet.add(new InetSocketAddress(instance.getIp(), instance.getPort()));
            }

            // 旧连接List
            List<InetSocketAddress> oldAddresses = SERVICE_LIST_MAP.get(serviceName);
            for (InetSocketAddress address : oldAddresses) {
                if (!addressSet.contains(address)) {
                    // 如果新连接Set不包含旧连接项
                    // 断开连接
                    manager.disconnect(address);
                }
                // 如果包含，在新连接Set移除该旧连接
                addressSet.remove(address);
            }

            for (InetSocketAddress address : addressSet) {
                // 创建连接
                manager.connect(address);
            }
        }
    }
}
