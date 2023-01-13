package me.hsgamer.bettergui.maskedgui.api;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.hscore.bukkit.gui.mask.Mask;

import java.util.UUID;

public interface WrappedMask extends Mask, MenuElement {
    default void refresh(UUID uuid) {
        // EMPTY
    }
}
