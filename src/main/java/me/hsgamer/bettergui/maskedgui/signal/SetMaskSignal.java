package me.hsgamer.bettergui.maskedgui.signal;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.signal.BaseSignal;

public class SetMaskSignal extends BaseSignal {
    private final String maskName;

    public SetMaskSignal(String name, Menu menu, String maskName) {
        super(name, menu);
        this.maskName = maskName;
    }

    public String getMaskName() {
        return maskName;
    }
}
