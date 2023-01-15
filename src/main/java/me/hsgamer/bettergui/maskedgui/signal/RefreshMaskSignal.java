package me.hsgamer.bettergui.maskedgui.signal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;

public class RefreshMaskSignal implements Signal {
    public final Menu menu;
    public final String id;

    public RefreshMaskSignal(Menu menu, String id) {
        this.menu = menu;
        this.id = id;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }
}
