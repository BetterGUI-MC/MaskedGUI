package me.hsgamer.bettergui.maskedgui;

import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;

public final class MaskedGUI extends PluginAddon {
    @Override
    public void onEnable() {
        MenuBuilder.INSTANCE.register(MaskedMenu::new, "masked");
    }
}
