package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.signal.SetMaskSignal;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SetMaskAction extends SignalAction {
    private final String signalName;

    public SetMaskAction(Plugin plugin, ActionBuilder.Input input) {
        super(plugin, input);
        signalName = input.option.trim();
    }

    @Override
    protected Signal createSignal(UUID uuid, String value) {
        return new SetMaskSignal(signalName, getMenu(), value);
    }
}
