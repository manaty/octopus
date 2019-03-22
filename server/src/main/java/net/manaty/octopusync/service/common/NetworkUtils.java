package net.manaty.octopusync.service.common;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkUtils {

    public static InetSocketAddress parseAddress(String s) {
        String[] parts = s.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid address string: '" + s + "'");
        }
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        return new InetSocketAddress(host, port);
    }

    public static String stringifyAddress(InetSocketAddress address) {
        String ipaddress = address.getHostString();
        int port = address.getPort();
        return ipaddress + ":" + port;
    }

    public static InetAddress getInetAddressFromNetworkInterfaces() {
        InetAddress selectedAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            outer:
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isMulticastAddress() && !inetAddress.isLoopbackAddress()
                            && inetAddress.getAddress().length == 4) {
                        selectedAddress = inetAddress;
                        break outer;
                    }
                }
            }

        } catch (SocketException e) {
            throw new IllegalStateException("Failed to retrieve network address", e);
        }
        // explicitly returning a loopback address here instead of null;
        // otherwise we'll depend on how JDK classes handle this,
        // e.g. java/net/Socket.java:635
        return (selectedAddress == null)? InetAddress.getLoopbackAddress() : selectedAddress;
    }
}
