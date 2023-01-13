package me.hsgamer.bettergui.maskedgui;

import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.config.TemplateMaskConfig;
import me.hsgamer.bettergui.maskedgui.mask.TemplateMask;
import me.hsgamer.bettergui.maskedgui.mask.WrappedAnimatedMask;
import me.hsgamer.bettergui.maskedgui.mask.WrappedMultiSlotMasks;
import me.hsgamer.bettergui.maskedgui.mask.WrappedSingleMask;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;

public final class MaskedGUI extends PluginAddon {
    private final MaskBuilder maskBuilder = new MaskBuilder();
    private final TemplateMaskConfig templateMaskConfig = new TemplateMaskConfig(this);

    @Override
    public void onEnable() {
        templateMaskConfig.setup();

        MenuBuilder.INSTANCE.register(config -> new MaskedMenu(this, config), "masked");

        maskBuilder.register(WrappedSingleMask::new, "single", "simple");
        maskBuilder.register(WrappedMultiSlotMasks::new, "multi-slots", "multislots", "multi-slot", "multislot", "multi");
        maskBuilder.register(input -> new WrappedAnimatedMask(this, input), "animated", "animate", "anim");
        maskBuilder.register(input -> new TemplateMask(this, input), "template", "temp");
    }

    @Override
    public void onReload() {
        templateMaskConfig.reload();
    }

    @Override
    public void onDisable() {
        templateMaskConfig.clear();
    }

    public MaskBuilder getMaskBuilder() {
        return maskBuilder;
    }

    public TemplateMaskConfig getTemplateMaskConfig() {
        return templateMaskConfig;
    }
}
