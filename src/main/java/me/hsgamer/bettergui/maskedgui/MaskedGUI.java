package me.hsgamer.bettergui.maskedgui;

import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.mask.WrappedAnimatedMask;
import me.hsgamer.bettergui.maskedgui.mask.WrappedMultiSlotMasks;
import me.hsgamer.bettergui.maskedgui.mask.WrappedSingleMask;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;

public final class MaskedGUI extends PluginAddon {
    private final MaskBuilder maskBuilder = new MaskBuilder();

    @Override
    public void onEnable() {
        MenuBuilder.INSTANCE.register(config -> new MaskedMenu(this, config), "masked");

        maskBuilder.register(WrappedSingleMask::new, "single", "simple");
        maskBuilder.register(WrappedMultiSlotMasks::new, "multi-slots", "multislots", "multi-slot", "multislot", "multi");
        maskBuilder.register(input -> new WrappedAnimatedMask(this, input), "animated", "animate", "anim");
    }

    public MaskBuilder getMaskBuilder() {
        return maskBuilder;
    }
}
