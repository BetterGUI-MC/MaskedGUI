package me.hsgamer.bettergui.maskedgui.signal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.BaseSignal;

public class ChangePageSignal extends BaseSignal {
    private final boolean next;

    public ChangePageSignal(String name, Menu menu, boolean next) {
        super(name, menu);
        this.next = next;
    }

    public boolean isNext() {
        return next;
    }
}
