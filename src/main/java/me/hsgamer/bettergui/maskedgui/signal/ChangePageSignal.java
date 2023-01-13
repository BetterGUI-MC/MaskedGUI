package me.hsgamer.bettergui.maskedgui.signal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;

public class ChangePageSignal implements Signal {
    public final Menu menu;
    public final String id;
    public final boolean next;

    public ChangePageSignal(Menu menu, String id, boolean next) {
        this.menu = menu;
        this.id = id;
        this.next = next;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }
}
