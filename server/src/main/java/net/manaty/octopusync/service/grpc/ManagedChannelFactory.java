package net.manaty.octopusync.service.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ManagedChannelFactory implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedChannelFactory.class);

    private final Set<ManagedChannel> channels;

    public ManagedChannelFactory() {
        this.channels = ConcurrentHashMap.newKeySet();
    }

    public ManagedChannel createChannel(String name, int port) {
        return createChannel(name, port, false);
    }

    public ManagedChannel createPlaintextChannel(String name, int port) {
        return createChannel(name, port, true);
    }

    public ManagedChannel createLocalChannel(int port) {
        return createChannel("localhost", port, false);
    }

    public ManagedChannel createLocalPlaintextChannel(int port) {
        return createChannel("localhost", port, true);
    }

    private ManagedChannel createChannel(String name, int port, boolean usePlaintext) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating gRPC channel to {}:{} (plaintext: {})", name, port, usePlaintext);
        }

        ManagedChannelBuilder channelBuilder = ManagedChannelBuilder.forAddress(name, port);
        if (usePlaintext) {
            channelBuilder = channelBuilder.usePlaintext();
        }
        ManagedChannel channel = channelBuilder.build();
        channels.add(channel);
        return channel;
    }

    @Override
    public void close() {
        Set<ManagedChannel> closingChannels = channels.stream()
                .filter(channel -> {
                    try {
                        channel.shutdownNow();
                        return true;
                    } catch (Exception e) {
                        LOGGER.error("Failed to issue shutdown on channel " + channel, e);
                        return false;
                    }
                })
                .collect(Collectors.toSet());

        closingChannels.forEach(channel -> {
            try {
                channel.awaitTermination(5000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to shutdown channel " + channel, e);
            }
        });
    }
}
