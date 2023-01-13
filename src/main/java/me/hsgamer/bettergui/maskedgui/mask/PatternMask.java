package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.MaskUtils;
import me.hsgamer.hscore.common.CollectionUtils;

import java.util.*;

public class PatternMask implements WrappedMask {
    private final MaskBuilder.Input input;
    private final Map<Button, List<Integer>> buttonSlotsMap = new HashMap<>();

    public PatternMask(MaskBuilder.Input input) {
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public Map<Integer, Button> generateButtons(UUID uuid) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        for (Map.Entry<Button, List<Integer>> entry : this.buttonSlotsMap.entrySet()) {
            Button button = entry.getKey();
            for (int slot : entry.getValue()) {
                buttonMap.put(slot, button);
            }
        }
        return buttonMap;
    }

    @Override
    public void refresh(UUID uuid) {
        buttonSlotsMap.keySet().stream()
                .filter(WrappedButton.class::isInstance)
                .map(WrappedButton.class::cast)
                .forEach(wrappedButton -> wrappedButton.refresh(uuid));
    }

    @Override
    public void init() {
        List<String> pattern = CollectionUtils.createStringListFromObject(input.options.get("pattern"), true);
        if (pattern.isEmpty()) return;

        Optional<Map<String, Object>> optionalButtonMap = MapUtil.castOptionalStringObjectMap(MapUtil.getIfFound(input.options, "button", "buttons"));
        if (!optionalButtonMap.isPresent()) return;
        Map<String, WrappedButton> buttonMap = MaskUtil.createButtons(this, optionalButtonMap.get());

        for (int y = 0; y < pattern.size(); y++) {
            String line = pattern.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == ' ') continue;
                WrappedButton button = buttonMap.get(String.valueOf(c));
                if (button == null) continue;
                buttonSlotsMap.computeIfAbsent(button, k -> new ArrayList<>()).add(MaskUtils.toSlot(x, y));
            }
        }
    }

    @Override
    public void stop() {
        buttonSlotsMap.keySet().forEach(Button::stop);
        buttonSlotsMap.clear();
    }

    @Override
    public String getName() {
        return input.name;
    }
}
