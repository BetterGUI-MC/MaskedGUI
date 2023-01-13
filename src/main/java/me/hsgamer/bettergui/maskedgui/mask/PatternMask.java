package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.maskedgui.api.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.MaskUtils;
import me.hsgamer.hscore.bukkit.gui.mask.impl.ButtonMapMask;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.*;

public class PatternMask extends BaseWrappedMask<ButtonMapMask> {
    public PatternMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ButtonMapMask createMask(Map<String, Object> section) {
        List<String> pattern = CollectionUtils.createStringListFromObject(section.get("pattern"), true);
        if (pattern.isEmpty()) return null;
        Map<String, List<Integer>> patternMap = new HashMap<>();
        for (int y = 0; y < pattern.size(); y++) {
            String line = pattern.get(y);
            for (int x = 0; x < line.length(); x++) {
                String key = String.valueOf(line.charAt(x));
                patternMap.computeIfAbsent(key, k -> new ArrayList<>()).add(MaskUtils.toSlot(x, y));
            }
        }

        Optional<Map<String, Object>> optionalButtonElement = MapUtil.castOptionalStringObjectMap(MapUtil.getIfFound(section, "button", "buttons"));
        if (!optionalButtonElement.isPresent()) return null;
        Map<String, WrappedButton> buttonElements = MaskUtil.createButtons(this, optionalButtonElement.get());

        Map<Button, List<Integer>> buttonMap = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : patternMap.entrySet()) {
            WrappedButton button = buttonElements.get(entry.getKey());
            if (button == null) continue;
            buttonMap.put(button, entry.getValue());
        }
        return new ButtonMapMask(getName(), buttonMap);
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        mask.getButtons().stream()
                .filter(WrappedButton.class::isInstance)
                .map(WrappedButton.class::cast)
                .forEach(button -> button.refresh(uuid));
    }
}