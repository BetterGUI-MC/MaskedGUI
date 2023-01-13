package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.WrappedMask;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.Mask;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.*;

public final class MaskUtil {
    private MaskUtil() {
        // EMPTY
    }

    public static Map<String, WrappedButton> createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap, String prefix, boolean initButton) {
        Map<String, WrappedButton> buttons = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : buttonMap.entrySet()) {
            String name = entry.getKey();
            Optional<Map<String, Object>> optionalValues = MapUtil.castOptionalStringObjectMap(entry.getValue());
            if (!optionalValues.isPresent()) continue;
            Map<String, Object> values = new CaseInsensitiveStringMap<>(optionalValues.get());
            ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(wrappedMask.getMenu(), prefix + name, values)).ifPresent(button -> {
                if (initButton) {
                    button.init();
                }
                buttons.put(name, button);
            });
        }
        return buttons;
    }

    public static Map<String, WrappedButton> createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap, boolean initButton) {
        return createButtons(wrappedMask, buttonMap, wrappedMask.getName() + "_button_", initButton);
    }

    public static Map<String, WrappedButton> createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap) {
        return createButtons(wrappedMask, buttonMap, true);
    }

    public static void refreshButtons(UUID uuid, Collection<Button> buttons) {
        buttons.stream()
                .filter(WrappedButton.class::isInstance)
                .map(WrappedButton.class::cast)
                .forEach(button -> button.refresh(uuid));
    }

    public static void refreshMasks(UUID uuid, Collection<Mask> masks) {
        masks.stream()
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .forEach(mask -> mask.refresh(uuid));
    }
}
