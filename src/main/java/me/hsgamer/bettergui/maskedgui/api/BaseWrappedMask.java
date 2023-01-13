package me.hsgamer.bettergui.maskedgui.api;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.Mask;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public abstract class BaseWrappedMask<T extends Mask> implements WrappedMask {
    protected final Menu menu;
    protected final String name;
    protected final Map<String, Object> options;
    protected T mask;

    protected BaseWrappedMask(MaskBuilder.Input input) {
        this.menu = input.menu;
        this.name = input.name;
        this.options = input.options;
    }

    protected abstract T createMask(Map<String, Object> section);

    public Map<String, Object> getOptions() {
        return options;
    }

    public T getMask() {
        return mask;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public Map<Integer, Button> generateButtons(UUID uuid) {
        return mask == null ? Collections.emptyMap() : mask.generateButtons(uuid);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init() {
        mask = createMask(options);
        if (mask != null) {
            mask.init();
        }
    }

    @Override
    public void stop() {
        if (mask != null) {
            mask.stop();
        }
    }
}
