package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class RefreshMaskAction extends SignalAction {
    public RefreshMaskAction(Plugin plugin, ActionBuilder.Input input) {
        super(plugin, input);
    }

    @Override
    protected Signal createSignal(UUID uuid, String value) {
        return new RefreshMaskSignal(getMenu(), value);
    }
}
