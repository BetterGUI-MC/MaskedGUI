package me.hsgamer.bettergui.maskedgui.action;

import me.hsgamer.bettergui.api.action.BaseAction;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.bettergui.maskedgui.signal.ChangePageSignal;
import me.hsgamer.hscore.task.BatchRunnable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ChangePageAction extends BaseAction {
    private final Plugin plugin;
    private final boolean next;

    public ChangePageAction(Plugin plugin, ActionBuilder.Input input, boolean next) {
        super(input);
        this.plugin = plugin;
        this.next = next;
    }

    @Override
    public void accept(UUID uuid, BatchRunnable.Process process) {
        String signalId = getReplacedString(uuid);
        Menu menu = getMenu();
        if (!(menu instanceof MaskedMenu)) {
            process.next();
            return;
        }
        MaskedMenu maskedMenu = (MaskedMenu) menu;
        ChangePageSignal signal = new ChangePageSignal(menu, signalId, next);
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
