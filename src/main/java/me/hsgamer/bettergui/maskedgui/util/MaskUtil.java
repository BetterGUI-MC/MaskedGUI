package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.WrappedMask;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MaskUtil {
    private MaskUtil() {
        // EMPTY
    }

    public static List<Button> createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap, String prefix, boolean initButton) {
        List<Button> buttons = new ArrayList<>();
        for (Map.Entry<String, Object> entry : buttonMap.entrySet()) {
            Optional<Map<String, Object>> optionalValues = MapUtil.castOptionalStringObjectMap(entry.getValue());
            if (!optionalValues.isPresent()) continue;
            Map<String, Object> values = new CaseInsensitiveStringMap<>(optionalValues.get());
            ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(wrappedMask.getMenu(), prefix + entry.getKey(), values)).ifPresent(button -> {
                if (initButton) {
                    button.init();
                }
                buttons.add(button);
            });
        }
        return buttons;
    }

    public static List<Button> createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap, boolean initButton) {
        return createButtons(wrappedMask, buttonMap, wrappedMask.getName() + "_button_", initButton);
    }

    public static List<Button> createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap) {
        return createButtons(wrappedMask, buttonMap, true);
    }
}
