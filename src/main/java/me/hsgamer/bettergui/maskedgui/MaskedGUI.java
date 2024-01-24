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

import me.hsgamer.bettergui.api.addon.GetLogger;
import me.hsgamer.bettergui.api.addon.GetPlugin;
import me.hsgamer.bettergui.api.addon.Reloadable;
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
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.checker.spigotmc.SpigotVersionChecker;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.expansion.extra.expansion.DataFolder;
import me.hsgamer.hscore.expansion.extra.expansion.GetClassLoader;
import me.hsgamer.hscore.license.common.LicenseChecker;
import me.hsgamer.hscore.license.common.LicenseResult;
import me.hsgamer.hscore.license.polymart.PolymartLicenseChecker;
import me.hsgamer.hscore.license.spigotmc.SpigotLicenseChecker;
import me.hsgamer.hscore.logger.common.LogLevel;

import java.io.File;

public final class MaskedGUI implements Expansion, DataFolder, GetPlugin, GetClassLoader, Reloadable, GetLogger {
    private TemplateConfig templateMaskConfig = new TemplateConfig(new File(getDataFolder(), "template"));

    @Override
    public void onEnable() {
        File file = new File(getDataFolder(), "template");
        if (!file.exists()) {
            file.mkdirs();
        }
        templateMaskConfig = new TemplateConfig(file);
        templateMaskConfig.setup();

        MenuBuilder.INSTANCE.register(MaskedMenu::new, "masked");

        ActionBuilder.INSTANCE.register(input -> new NextPageAction(input, true), "next-page");
        ActionBuilder.INSTANCE.register(input -> new NextPageAction(input, false), "previous-page", "back-page");
        ActionBuilder.INSTANCE.register(SetPageAction::new, "set-page", "page");
        ActionBuilder.INSTANCE.register(RefreshMaskAction::new, "refresh-mask");
        ActionBuilder.INSTANCE.register(SetMaskAction::new, "set-mask");

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
        MaskBuilder.INSTANCE.register(SimpleValueListMask::new, "simple-value-list", "value-list");
        MaskBuilder.INSTANCE.register(KeyValueListMask::new, "key-value-list", "map-list");
        MaskBuilder.INSTANCE.register(FilteredButtonPaginatedMask::new, "filtered-button-paginated", "filtered-button-paginate", "filtered-button-pag", "filtered-button-page", "filtered-button-p");

        new SpigotVersionChecker(107475).getVersion().whenComplete((output, throwable) -> {
            if (throwable != null) {
                getLogger().log(LogLevel.WARN, "Cannot check the latest version of MaskedGUI", throwable);
            } else if (this.getExpansionClassLoader().getDescription().getVersion().equalsIgnoreCase(output)) {
                getLogger().log(LogLevel.INFO, "You are using the latest version of MaskedGUI");
            } else {
                getLogger().log(LogLevel.WARN, "You are using an outdated version of MaskedGUI. Please update to " + output);
            }
        });

        LicenseChecker licenseChecker = PolymartLicenseChecker.isAvailable()
                ? new PolymartLicenseChecker("3388", true, true)
                : new SpigotLicenseChecker("107475");
        Scheduler.current().async().runTask(() -> {
            LicenseResult result = licenseChecker.checkLicense();
            switch (result.getStatus()) {
                case VALID:
                    getLogger().log(LogLevel.INFO, "Thank you for supporting MaskedGUI. Your support is greatly appreciated");
                    break;
                case INVALID:
                    getLogger().log(LogLevel.WARN, "Thank you for using MaskedGUI");
                    getLogger().log(LogLevel.WARN, "If you like this addon, please consider supporting it by purchasing from one of these platforms:");
                    getLogger().log(LogLevel.WARN, "- SpigotMC: https://www.spigotmc.org/resources/maskedgui.107475/");
                    getLogger().log(LogLevel.WARN, "- Polymart: https://polymart.org/resource/maskedgui.3388");
                    break;
                case OFFLINE:
                    getLogger().log(LogLevel.WARN, "Cannot check your license for MaskedGUI. Please check your internet connection");
                    getLogger().log(LogLevel.WARN, "Note: You can still use this addon without a license, and there is no limit on the features");
                    getLogger().log(LogLevel.WARN, "However, if you like this addon, please consider supporting it by purchasing it from one of these platforms:");
                    getLogger().log(LogLevel.WARN, "- SpigotMC: https://www.spigotmc.org/resources/maskedgui.107475/");
                    getLogger().log(LogLevel.WARN, "- Polymart: https://polymart.org/resource/maskedgui.3388");
                    break;
                case UNKNOWN:
                    getLogger().log(LogLevel.WARN, "Cannot check your license for MaskedGUI. Please try again later");
                    getLogger().log(LogLevel.WARN, "Note: You can still use this addon without a license, and there is no limit on the features");
                    break;
            }
        });
    }

    @Override
    public void onReload() {
        templateMaskConfig.clear();
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
