package me.hsgamer.bettergui.maskedgui.signal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.BaseSignal;

public class SetPageSignal extends BaseSignal {
    private final int page;

    public SetPageSignal(String name, Menu menu, int page) {
        super(name, menu);
        this.page = page;
    }

    public int getPage() {
        return page;
    }
}
