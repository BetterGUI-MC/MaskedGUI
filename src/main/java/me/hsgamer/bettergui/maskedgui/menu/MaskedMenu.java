package me.hsgamer.bettergui.maskedgui.menu;

import me.hsgamer.bettergui.menu.BaseInventoryMenu;
import me.hsgamer.hscore.bukkit.gui.advanced.AdvancedButtonMap;
import me.hsgamer.hscore.config.Config;

public class MaskedMenu extends BaseInventoryMenu<AdvancedButtonMap> {
    public MaskedMenu(Config config) {
        super(config);
    }

    @Override
    protected AdvancedButtonMap createButtonMap(Config config) {
        AdvancedButtonMap buttonMap = new AdvancedButtonMap();
        return buttonMap;
    }
}
