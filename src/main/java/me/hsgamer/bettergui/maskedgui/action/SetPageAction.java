package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.signal.SetPageSignal;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SetPageAction extends SignalAction {
    private final String signalName;

    public SetPageAction(Plugin plugin, ActionBuilder.Input input) {
        super(plugin, input);
        signalName = input.option.trim();
    }

    @Override
    protected Signal createSignal(UUID uuid, String value) {
        int page;
        try {
            page = Integer.parseInt(value);
        } catch (Exception e) {
            page = 0;
        }
        return new SetPageSignal(signalName, getMenu(), page);
    }
}
