package cn.zhaokanglun.myrpc.spring.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("myrpc")
public class MyrpcProperties {
    private int port;

    private String discoveryAddress;

    private int mode;

    private String serverAddressForSimpleMode;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDiscoveryAddress() {
        return discoveryAddress;
    }

    public void setDiscoveryAddress(String discoveryAddress) {
        this.discoveryAddress = discoveryAddress;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getServerAddressForSimpleMode() {
        return serverAddressForSimpleMode;
    }

    public void setServerAddressForSimpleMode(String serverAddressForSimpleMode) {
        this.serverAddressForSimpleMode = serverAddressForSimpleMode;
    }

    @Override
    public String toString() {
        return "MyrpcProperties{" +
                "port=" + port +
                ", discoveryAddress='" + discoveryAddress + '\'' +
                ", mode=" + mode +
                ", serverAddressForSimpleMode='" + serverAddressForSimpleMode + '\'' +
                '}';
    }
}
