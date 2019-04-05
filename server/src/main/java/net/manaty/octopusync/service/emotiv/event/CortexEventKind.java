package net.manaty.octopusync.service.emotiv.event;

import java.util.Objects;

public enum CortexEventKind {

    EEG,

    MOT,

    COM,

    FAC,

    MET,

    DEV,

    POW,

    SYS;

    public static CortexEventKind forName(String name) {
        Objects.requireNonNull(name);
        for (CortexEventKind eventKind : values()) {
            if (eventKind.name().equalsIgnoreCase(name)) {
                return eventKind;
            }
        }
        throw new IllegalArgumentException("Invalid event kind name: " + name);
    }
}
