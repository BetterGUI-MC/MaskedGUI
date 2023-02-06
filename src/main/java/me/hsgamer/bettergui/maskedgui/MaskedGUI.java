package me.hsgamer.bettergui.maskedgui;

import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.maskedgui.action.ChangePageAction;
import me.hsgamer.bettergui.maskedgui.action.RefreshMaskAction;
import me.hsgamer.bettergui.maskedgui.action.SetMaskAction;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.config.TemplateMaskConfig;
import me.hsgamer.bettergui.maskedgui.mask.*;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;

import java.util.logging.Level;

public final class MaskedGUI extends PluginAddon {
    private final TemplateMaskConfig templateMaskConfig = new TemplateMaskConfig(this);

    @Override
    public void onEnable() {
        templateMaskConfig.setup();

        MenuBuilder.INSTANCE.register(MaskedMenu::new, "masked");

        ActionBuilder.INSTANCE.register(input -> new ChangePageAction(getPlugin(), input, true), "next-page");
        ActionBuilder.INSTANCE.register(input -> new ChangePageAction(getPlugin(), input, false), "previous-page", "back-page");
        ActionBuilder.INSTANCE.register(input -> new RefreshMaskAction(getPlugin(), input), "refresh-mask");
        ActionBuilder.INSTANCE.register(input -> new SetMaskAction(getPlugin(), input), "set-mask");

        MaskBuilder.INSTANCE.setDefaultMaskType("simple");
        MaskBuilder.INSTANCE.register(WrappedSimpleMask::new, "simple");
        MaskBuilder.INSTANCE.register(WrappedMultiSlotMasks::new, "multi-slots", "multislots", "multi-slot", "multislot", "multi");
        MaskBuilder.INSTANCE.register(WrappedAnimatedMask::new, "animated", "animate", "anim");
        MaskBuilder.INSTANCE.register(input -> new TemplateMask(this, input), "template", "temp");
        MaskBuilder.INSTANCE.register(PatternMask::new, "pattern", "pat");
        MaskBuilder.INSTANCE.register(WrappedButtonPaginatedMask::new, "button-paginated", "button-paginate", "button-pag", "button-page", "button-p");
        MaskBuilder.INSTANCE.register(WrappedSequencePaginatedMask::new, "sequence-paginated", "sequence-paginate", "sequence-pag", "sequence-page", "sequence-p");
        MaskBuilder.INSTANCE.register(WrappedMaskPaginatedMask::new, "mask-paginated", "mask-paginate", "mask-pag", "mask-page", "mask-p");
        MaskBuilder.INSTANCE.register(WrappedHybridMask::new, "hybrid", "hyb", "combine");
        MaskBuilder.INSTANCE.register(ProgressMask::new, "progress", "prog");
        MaskBuilder.INSTANCE.register(WrappedListMask::new, "list");
        MaskBuilder.INSTANCE.register(WrappedPredicateMask::new, "predicate", "requirement");
        MaskBuilder.INSTANCE.register(input -> new OneTimeAnimatedMask(this, input), "one-time-animated", "one-time-animate", "one-time-anim", "animated-one-time", "animate-one-time", "anim-one-time", "animated-once", "animate-once", "anim-once");
        MaskBuilder.INSTANCE.register(SwitchMask::new, "switch");

        new SpigotVersionChecker(107475).getVersion().whenComplete((output, throwable) -> {
            if (throwable != null) {
                getPlugin().getLogger().log(Level.WARNING, "Cannot check the latest version of MaskedGUI", throwable);
            } else if (this.getDescription().getVersion().equalsIgnoreCase(output)) {
                getPlugin().getLogger().info("You are using the latest version of MaskedGUI");
            } else {
                getPlugin().getLogger().warning("You are using an outdated version of MaskedGUI. Please update to " + output);
            }
        });
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
