package me.hsgamer.bettergui.maskedgui.menu;

import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.bettergui.maskedgui.api.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.menu.BaseInventoryMenu;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.advanced.AdvancedButtonMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.config.Config;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MaskedMenu extends BaseInventoryMenu<AdvancedButtonMap> {
    private final MaskedGUI addon;

    public MaskedMenu(MaskedGUI addon, Config config) {
        super(config);
        this.addon = addon;
    }

    @Override
    protected AdvancedButtonMap createButtonMap(Config config) {
        AdvancedButtonMap buttonMap = new AdvancedButtonMap();
        for (Map.Entry<String, Object> entry : config.getNormalizedValues(false).entrySet()) {
            String key = entry.getKey();
            Optional<Map<String, Object>> optionalValue = MapUtil.castOptionalStringObjectMap(entry.getValue());
            if (key.equalsIgnoreCase("menu-settings") || !optionalValue.isPresent()) continue;

            Map<String, Object> values = new CaseInsensitiveStringMap<>(optionalValue.get());
            addon.getMaskBuilder()
                    .build(new MaskBuilder.Input(this, "mask_" + key, values))
                    .ifPresent(mask -> {
                        mask.init();
                        buttonMap.addMask(mask);
                    });
        }
        return buttonMap;
    }

    @Override
    protected void refreshButtonMapOnCreate(AdvancedButtonMap buttonMap, UUID uuid) {
        buttonMap.getMasks()
                .stream()
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .forEach(mask -> mask.refresh(uuid));
    }
}
