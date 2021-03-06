package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@BQConfig("Contains generic configuration properties of OctopuSync server.")
public class ServerConfiguration {

    private String address;
    private String reportRoot;
    private boolean shouldNormalizeEegValues;

    @BQConfigProperty("IP address or hostname to bind all network services to." +
            "If not specified, appropriate IPv4 address will be selected automatically," +
            "falling back to localhost, if there are no suitable network interfaces.")
    public void setAddress(String address) {
        this.address = address;
    }

    @BQConfigProperty("Path to report root directory." +
            " Reports generated through Reporting Web API will be stored in this directory." +
            " If the specified directory does not exist at the moment of report generation," +
            " then it will be created automatically.")
    public void setReportRoot(String reportRoot) {
        this.reportRoot = reportRoot;
    }

    @BQConfigProperty("Indicates, if EEG values in the report should be normalized" +
            " (4000.0 subtracted from each value)")
    public void setShouldNormalizeEegValues(boolean shouldNormalizeEegValues) {
        this.shouldNormalizeEegValues = shouldNormalizeEegValues;
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

    public Path getReportRoot() {
        Objects.requireNonNull(reportRoot);
        return Paths.get(reportRoot);
    }

    public boolean shouldNormalizeEegValues() {
        return shouldNormalizeEegValues;
    }
}
