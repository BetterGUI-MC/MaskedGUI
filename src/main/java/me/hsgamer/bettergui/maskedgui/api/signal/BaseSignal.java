package me.hsgamer.bettergui.maskedgui.api.signal;

import me.hsgamer.bettergui.api.menu.Menu;

public class BaseSignal implements Signal {
    private final String name;
    private final Menu menu;

    public BaseSignal(String name, Menu menu) {
        this.name = name;
        this.menu = menu;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }
}
