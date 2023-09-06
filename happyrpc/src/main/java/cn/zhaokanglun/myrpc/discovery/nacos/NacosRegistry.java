package cn.zhaokanglun.myrpc.discovery.nacos;

import cn.zhaokanglun.myrpc.discovery.Registry;
import cn.zhaokanglun.myrpc.exception.MyRpcException;
import cn.zhaokanglun.myrpc.exception.NetworkException;
import cn.zhaokanglun.myrpc.network.ClientConnectionManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static cn.zhaokanglun.myrpc.cache.Storage.SERVICE_LIST_MAP;
import static cn.zhaokanglun.myrpc.cache.Storage.SERVICE_MAP;

public class NacosRegistry implements Registry {
    private static final Logger logger = LoggerFactory.getLogger(NacosRegistry.class);
    private final NamingService naming;
    private final InetSocketAddress local;

    private final UpdateListener listener;

    private final ClientConnectionManager manager;

    public NacosRegistry(String nacosAddr, InetSocketAddress local, ClientConnectionManager manager) throws MyRpcException {
        try {
            naming = NamingFactory.createNamingService(nacosAddr);
        } catch (NacosException e) {
            throw new NetworkException(e);
        }
        this.local = local;
        this.listener = new UpdateListener(manager);
        this.manager = manager;
    }

    @Override
    public void register(Object serviceObj) throws MyRpcException {
        try {
            String serviceName = serviceObj.getClass().getInterfaces()[0].getSimpleName();
            logger.info("service " + serviceName + " registered for address " + local);
            naming.registerInstance(serviceName, local.getHostName(), local.getPort());
            SERVICE_MAP.put(serviceName, serviceObj);
        } catch (NacosException e) {
            throw new NetworkException(e);
        }
    }

    @Override
    public void deregister(Object serviceObj) throws MyRpcException {
        try {
            String serviceName = serviceObj.getClass().getInterfaces()[0].getSimpleName();
            logger.info("deregister service: " + serviceName);
            naming.deregisterInstance(serviceName, local.getHostName(), local.getPort());
        } catch (NacosException e) {
            throw new NetworkException(e);
        }
    }

    @Override
    public List<InetSocketAddress> find(String serviceName) throws MyRpcException {
        try {
            List<Instance> instances = naming.getAllInstances(serviceName);
            List<InetSocketAddress> addresses = new ArrayList<>();
            for (Instance instance : instances) {
                addresses.add(new InetSocketAddress(instance.getIp(), instance.getPort()));
            }
            return addresses;
        } catch (NacosException e) {
            throw new NetworkException(e);
        }
    }

    @Override
    public void subscribe(String serviceName) throws MyRpcException {
        try {
            logger.info("subscribing service: " + serviceName);
            List<InetSocketAddress> addresses = find(serviceName);
            logger.info("get addresses of service " + serviceName + ": " + addresses);
            manager.connectToList(addresses);
            SERVICE_LIST_MAP.put(serviceName, addresses);
            naming.subscribe(serviceName, listener);
        } catch (NacosException e) {
            throw new NetworkException(e);
        }
    }

    @Override
    public void unsubscribe(String serviceName) throws MyRpcException {
        try {
            logger.info("subscribing service: " + serviceName);
            naming.unsubscribe(serviceName, listener);
            List<InetSocketAddress> addresses = SERVICE_LIST_MAP.remove(serviceName);
            for (InetSocketAddress address : addresses) {
                manager.disconnect(address);
            }
        } catch (NacosException e) {
            throw new NetworkException(e);
        }
    }
}
