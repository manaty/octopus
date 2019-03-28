package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;

@BQConfig
public class ServerConfiguration {

    private String address;
    private String cortexServerAddress;
    private Map<String, String> headsetCodes;

    @BQConfigProperty("IP address or hostname to bind all network services to." +
            "If not specified, appropriate IPv4 address will be selected automatically," +
            "falling back to localhost, if there are no suitable network interfaces.")
    public void setAddress(String address) {
        this.address = address;
    }

    @BQConfigProperty
    public void setCortexServerAddress(String cortexServerAddress) {
        this.cortexServerAddress = cortexServerAddress;
    }

    @BQConfigProperty("Mapping of human readable codes to headset IDs")
    public void setHeadsetCodes(Map<String, String> headsetCodes) {
        this.headsetCodes = headsetCodes;
    }

    public InetAddress resolveAddress() {
        if (address == null) {
            return NetworkUtils.getInetAddressFromNetworkInterfaces();
        } else if (address.isEmpty()) {
            throw new IllegalArgumentException("Address string is empty");
        }
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Failed to resolve hostname: " + address);
        }
    }

    public InetSocketAddress resolveCortexServerAddress() {
        if (cortexServerAddress == null || cortexServerAddress.isEmpty()) {
            throw new IllegalStateException("Missing Cortex server address");
        }
        return NetworkUtils.parseAddress(cortexServerAddress);
    }
}
