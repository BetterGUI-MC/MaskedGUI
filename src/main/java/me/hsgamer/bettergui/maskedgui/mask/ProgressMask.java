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
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.bettergui.maskedgui.util.MaskSlotUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProgressMask implements WrappedMask {
    private final Menu menu;
    private final String name;
    private final Map<String, Object> section;
    private Function<UUID, List<Position>> maskSlot = uuid -> Collections.emptyList();
    private String currentValue = "0";
    private String maxValue = "100";
    private Button completeButton = (uuid, actionItem) -> false;
    private Button incompleteButton = (uuid, actionItem) -> false;

    public ProgressMask(MaskBuilder.Input input) {
        this.menu = input.menu;
        this.name = input.name;
        this.section = input.options;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        String parsedCurrentValue = StringReplacerApplier.replace(currentValue, uuid, this);
        String parsedMaxValue = StringReplacerApplier.replace(maxValue, uuid, this);
        List<Position> slots = maskSlot.apply(uuid);

        double current = Validate.getNumber(parsedCurrentValue).map(Number::doubleValue).orElse(0.0);
        double max = Validate.getNumber(parsedMaxValue).map(Number::doubleValue).orElse(100.0);

        int slotsSize = slots.size();
        int completeSize = max <= 0 || current < 0 ? 0 : (int) Math.round(current / max * slotsSize);
        completeSize = Math.min(completeSize, slotsSize);

        Map<Position, Consumer<ActionItem>> buttonMap = new HashMap<>();
        for (int i = 0; i < completeSize; i++) {
            buttonMap.put(slots.get(i), completeButton.apply(uuid));
        }
        for (int i = completeSize; i < slotsSize; i++) {
            buttonMap.put(slots.get(i), incompleteButton.apply(uuid));
        }
        return buttonMap;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public void refresh(UUID uuid) {
        if (completeButton instanceof WrappedButton) {
            ((WrappedButton) completeButton).refresh(uuid);
        }
        if (incompleteButton instanceof WrappedButton) {
            ((WrappedButton) incompleteButton).refresh(uuid);
        }
    }

    @Override
    public void init() {
        currentValue = Objects.toString(MapUtils.getIfFoundOrDefault(section, currentValue, "current-value", "current"), currentValue);
        maxValue = Objects.toString(MapUtils.getIfFoundOrDefault(section, maxValue, "max-value", "max"), maxValue);
        maskSlot = MaskSlotUtil.of(section, this);

        completeButton = MapUtils.castOptionalStringObjectMap(MapUtils.getIfFound(section, "complete-button", "complete", "current-button"))
                .flatMap(map -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(menu, name + "_complete_button", map)))
                .<Button>map(ButtonUtil.CraftUXButton::new)
                .orElse(completeButton);
        Element.handleIfElement(completeButton, Element::init);

        incompleteButton = MapUtils.castOptionalStringObjectMap(MapUtils.getIfFound(section, "incomplete-button", "incomplete", "max-button"))
                .flatMap(map -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(menu, name + "_incomplete_button", map)))
                .<Button>map(ButtonUtil.CraftUXButton::new)
                .orElse(incompleteButton);
        Element.handleIfElement(incompleteButton, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(completeButton, Element::stop);
        Element.handleIfElement(incompleteButton, Element::stop);
    }
}
