/*
   Copyright 2023-2023 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package me.hsgamer.bettergui.maskedgui;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.builder.ActionBuilder;
import me.hsgamer.bettergui.builder.MenuBuilder;
import me.hsgamer.bettergui.config.TemplateConfig;
import me.hsgamer.bettergui.maskedgui.action.NextPageAction;
import me.hsgamer.bettergui.maskedgui.action.RefreshMaskAction;
import me.hsgamer.bettergui.maskedgui.action.SetMaskAction;
import me.hsgamer.bettergui.maskedgui.action.SetPageAction;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.mask.*;
import me.hsgamer.bettergui.maskedgui.menu.MaskedMenu;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;

import java.io.File;
import java.util.logging.Level;

public final class MaskedGUI extends PluginAddon {
    private final TemplateConfig templateMaskConfig = new TemplateConfig(new File(getDataFolder(), "template"));

    @Override
    public void onEnable() {
        templateMaskConfig.setIncludeMenuInTemplate(BetterGUI.getInstance().getMainConfig().includeMenuInTemplate);
        templateMaskConfig.setup();

        MenuBuilder.INSTANCE.register(MaskedMenu::new, "masked");

        ActionBuilder.INSTANCE.register(input -> new NextPageAction(getPlugin(), input, true), "next-page");
        ActionBuilder.INSTANCE.register(input -> new NextPageAction(getPlugin(), input, false), "previous-page", "back-page");
        ActionBuilder.INSTANCE.register(input -> new SetPageAction(getPlugin(), input), "set-page", "page");
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
        MaskBuilder.INSTANCE.register(OneTimeAnimatedMask::new, "one-time-animated", "one-time-animate", "one-time-anim", "animated-one-time", "animate-one-time", "anim-one-time", "animated-once", "animate-once", "anim-once");
        MaskBuilder.INSTANCE.register(SwitchMask::new, "switch");
        MaskBuilder.INSTANCE.register(PlayerListMask::new, "player-list", "playerlist", "players");

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
        templateMaskConfig.clear();
        templateMaskConfig.setIncludeMenuInTemplate(BetterGUI.getInstance().getMainConfig().includeMenuInTemplate);
        templateMaskConfig.setup();
    }

    @Override
    public void onDisable() {
        templateMaskConfig.clear();
    }

    public TemplateConfig getTemplateMaskConfig() {
        return templateMaskConfig;
    }
}
