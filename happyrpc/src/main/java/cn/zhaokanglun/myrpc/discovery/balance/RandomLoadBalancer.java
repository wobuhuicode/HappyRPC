package cn.zhaokanglun.myrpc.discovery.balance;

import cn.zhaokanglun.myrpc.discovery.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

import static cn.zhaokanglun.myrpc.cache.Storage.SERVICE_LIST_MAP;

public class RandomLoadBalancer implements LoadBalancer {
    private volatile static RandomLoadBalancer instance;

    public static RandomLoadBalancer getInstance() {
        if (instance == null) {
            synchronized (RandomLoadBalancer.class) {
                if (instance == null) {
                    instance = new RandomLoadBalancer();
                }
            }
        }

        return instance;
    }

    private final Random random;

    private RandomLoadBalancer() {
        random = new Random(66621813);
    }

    @Override
    public InetSocketAddress getOneAddress(String serviceName) {
        List<InetSocketAddress> list = SERVICE_LIST_MAP.get(serviceName);
        if (list.isEmpty()) return null;
        int index = random.nextInt(list.size());
        return list.get(index);
    }

    @Override
    public String toString() {
        return "Random Load Balancer";
    }
}
