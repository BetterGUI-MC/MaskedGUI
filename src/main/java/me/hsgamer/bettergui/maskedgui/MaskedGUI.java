package me.hsgamer.bettergui.maskedgui;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.maskedgui.action.ChangePageAction;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.config.TemplateMaskConfig;
import me.hsgamer.bettergui.maskedgui.mask.*;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;

public final class MaskedGUI extends PluginAddon {
    private final TemplateMaskConfig templateMaskConfig = new TemplateMaskConfig(this);

    @Override
    public void onEnable() {
        templateMaskConfig.setup();

        MenuBuilder.INSTANCE.register(MaskedMenu::new, "masked");

        ActionBuilder.INSTANCE.register(input -> new ChangePageAction(getPlugin(), input, true), "next-page");
        ActionBuilder.INSTANCE.register(input -> new ChangePageAction(getPlugin(), input, false), "previous-page", "back-page");

        MaskBuilder.INSTANCE.register(WrappedSingleMask::new, "single", "simple");
        MaskBuilder.INSTANCE.register(WrappedMultiSlotMasks::new, "multi-slots", "multislots", "multi-slot", "multislot", "multi");
        MaskBuilder.INSTANCE.register(input -> new WrappedAnimatedMask(this, input), "animated", "animate", "anim");
        MaskBuilder.INSTANCE.register(input -> new TemplateMask(this, input), "template", "temp");
        MaskBuilder.INSTANCE.register(PatternMask::new, "pattern", "pat");
    }

    @Override
    public void onReload() {
        templateMaskConfig.reload();
    }

    @Override
    public void onDisable() {
        templateMaskConfig.clear();
    }

    public TemplateMaskConfig getTemplateMaskConfig() {
        return templateMaskConfig;
    }
}
