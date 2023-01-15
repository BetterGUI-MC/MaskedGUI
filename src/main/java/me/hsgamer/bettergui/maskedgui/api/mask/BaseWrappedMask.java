package me.hsgamer.bettergui.maskedgui.api.mask;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.Mask;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public abstract class BaseWrappedMask<T extends Mask> implements WrappedMask {
    protected final MaskBuilder.Input input;
    protected T mask;

    protected BaseWrappedMask(MaskBuilder.Input input) {
        this.input = input;
    }

    protected abstract T createMask(Map<String, Object> section);

    public Map<String, Object> getOptions() {
        return input.options;
    }

    public T getMask() {
        return mask;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public Map<Integer, Button> generateButtons(UUID uuid) {
        return mask == null ? Collections.emptyMap() : mask.generateButtons(uuid);
    }

    @Override
    public boolean canView(UUID uuid) {
        return mask != null && mask.canView(uuid);
    }

    @Override
    public String getName() {
        return input.name;
    }

    @Override
    public void init() {
        mask = createMask(input.options);
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