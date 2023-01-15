package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.signal.ChangePageSignal;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ChangePageAction extends SignalAction {
    private final boolean next;

    public ChangePageAction(Plugin plugin, ActionBuilder.Input input, boolean next) {
        super(plugin, input);
        this.next = next;
    }

    @Override
    protected Signal createSignal(UUID uuid, String value) {
        return new ChangePageSignal(value, getMenu(), next);
    }

    @Override
    protected boolean shouldBeTrimmed() {
        return true;
    }
}
