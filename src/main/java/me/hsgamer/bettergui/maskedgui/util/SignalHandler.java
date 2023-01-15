package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.util.MapUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class SignalHandler {
    private final Map<Class<?>, BiConsumer<UUID, Signal>> signalMap = new HashMap<>();
    private String signalName = "";

    public void handle(UUID uuid, Signal signal) {
        if (!signal.getName().equals(this.signalName)) return;
        if (signalMap.containsKey(signal.getClass())) {
            signalMap.get(signal.getClass()).accept(uuid, signal);
        }
    }

    public SignalHandler setSignal(String signal) {
        this.signalName = signal;
        return this;
    }

    public SignalHandler setSignal(Map<String, Object> options, String defaultName) {
        this.signalName = Objects.toString(MapUtil.getIfFoundOrDefault(options, defaultName, "signal", "signal-id"));
        return this;
    }

    public <T> SignalHandler addHandler(Class<T> signalClass, BiConsumer<UUID, T> consumer) {
        signalMap.put(signalClass, (uuid, signal) -> {
            if (signalClass.isInstance(signal)) {
                consumer.accept(uuid, signalClass.cast(signal));
            }
        });
        return this;
    }

    public void clear() {
        signalMap.clear();
    }
}
