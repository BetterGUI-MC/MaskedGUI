package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract class SignalAction extends BaseAction {
    private final Plugin plugin;

    protected SignalAction(Plugin plugin, ActionBuilder.Input input) {
        super(input);
        this.plugin = plugin;
    }

    protected abstract Signal createSignal(UUID uuid, String value);

    @Override
    public void accept(UUID uuid, BatchRunnable.Process process) {
        String value = getReplacedString(uuid);
        Menu menu = getMenu();
        if (!(menu instanceof MaskedMenu)) {
            process.next();
            return;
        }
        MaskedMenu maskedMenu = (MaskedMenu) menu;
        Signal signal = createSignal(uuid, value);
        Bukkit.getScheduler().runTask(plugin, () -> {
            maskedMenu.handleSignal(uuid, signal);
            process.next();
        });
    }

    @Override
    protected boolean shouldBeTrimmed() {
        return true;
    }
}
