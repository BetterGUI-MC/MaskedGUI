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
package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.object.InventoryPosition;
import me.hsgamer.hscore.minecraft.gui.object.InventorySize;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PatternMask implements WrappedMask {
    private final MaskBuilder.Input input;
    private final Map<InventoryPosition, Button> buttonMap = new HashMap<>();

    public PatternMask(MaskBuilder.Input input) {
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public Optional<Map<Integer, Button>> generateButtons(@NotNull UUID uuid, @NotNull InventorySize inventorySize) {
        Map<Integer, Button> buttons = buttonMap.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toSlot(inventorySize), Map.Entry::getValue, (a, b) -> b));
        return Optional.of(buttons);
    }

    @Override
    public @NotNull String getName() {
        return input.name;
    }

    @Override
    public void init() {
        List<String> pattern = CollectionUtils.createStringListFromObject(input.options.get("pattern"));
        if (pattern.isEmpty()) return;

        Map<Character, List<InventoryPosition>> patternMap = new HashMap<>();
        for (int y = 0; y < pattern.size(); y++) {
            String line = pattern.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                c = c == '.' ? ' ' : c;
                patternMap.computeIfAbsent(c, k -> new ArrayList<>()).add(InventoryPosition.of(x, y));
            }
        }

        Map<String, WrappedButton> buttonElements = ButtonUtil.createChildButtons(this, input.options).buttonMap();

        for (Map.Entry<String, WrappedButton> entry : buttonElements.entrySet()) {
            String keyString = entry.getKey();
            char key = keyString.isEmpty() ? ' ' : keyString.charAt(0);
            List<InventoryPosition> slots = patternMap.get(key);
            if (slots != null) {
                for (InventoryPosition position : slots) {
                    buttonMap.put(position, entry.getValue());
                }
            }
        }

        buttonMap.values().forEach(Button::init);
    }

    @Override
    public void stop() {
        buttonMap.values().forEach(Button::stop);
        buttonMap.clear();
    }

    @Override
    public void refresh(UUID uuid) {
        ButtonUtil.refreshButtons(uuid, buttonMap.values());
    }
}
