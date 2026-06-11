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

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Position;
import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.minecraft.gui.object.InventoryPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PatternMask implements WrappedMask {
    private final MaskBuilder.Input input;
    private final Map<Position, Button> buttonMap = new HashMap<>();

    public PatternMask(MaskBuilder.Input input) {
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        return buttonMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().apply(uuid)
        ));
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
                for (InventoryPosition inventoryPosition : slots) {
                    buttonMap.put(Position.of(inventoryPosition.getX(), inventoryPosition.getY()), new ButtonUtil.CraftUXButton(entry.getValue()));
                }
            }
        }

        Element.handleIfElement(buttonMap.values(), Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(buttonMap.values(), Element::stop);
        buttonMap.clear();
    }

    @Override
    public void refresh(UUID uuid) {
        ButtonUtil.refreshCraftUXButtons(uuid, buttonMap.values());
    }
}
