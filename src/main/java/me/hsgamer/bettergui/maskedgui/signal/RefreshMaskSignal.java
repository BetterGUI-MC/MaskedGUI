package me.hsgamer.bettergui.maskedgui.signal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.BaseSignal;

public class RefreshMaskSignal extends BaseSignal {
    public RefreshMaskSignal(String name, Menu menu) {
        super(name, menu);
    }
}
