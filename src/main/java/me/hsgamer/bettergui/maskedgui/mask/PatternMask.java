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
    private final List<Button> buttons = new ArrayList<>();
    private final Map<Integer, Button> buttonMap = new HashMap<>();

    public PatternMask(MaskBuilder.Input input) {
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public Map<Integer, Button> generateButtons(UUID uuid) {
        return buttonMap;
    }

    @Override
    public void refresh(UUID uuid) {
        buttons.stream()
                .filter(WrappedButton.class::isInstance)
                .map(WrappedButton.class::cast)
                .forEach(wrappedButton -> wrappedButton.refresh(uuid));
    }

    @Override
    public void init() {
        List<String> pattern = CollectionUtils.createStringListFromObject(input.options.get("pattern"), true);
        if (pattern.isEmpty()) return;

        Optional<Map<String, Object>> optionalButtonElement = MapUtil.castOptionalStringObjectMap(MapUtil.getIfFound(input.options, "button", "buttons"));
        if (!optionalButtonElement.isPresent()) return;
        Map<String, WrappedButton> buttonElements = MaskUtil.createButtons(this, optionalButtonElement.get());

        for (int y = 0; y < pattern.size(); y++) {
            String line = pattern.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == ' ') continue;
                WrappedButton button = buttonElements.get(String.valueOf(c));
                if (button == null) continue;
                buttons.add(button);
                buttonMap.put(MaskUtils.toSlot(x, y), button);
            }
        }
    }

    @Override
    public void stop() {
        buttons.forEach(Button::stop);
        buttonMap.clear();
    }

    @Override
    public String getName() {
        return input.name;
    }
}
