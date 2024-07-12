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
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.slot.WrappedMaskSlot;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.mask.MaskSlot;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProgressMask implements WrappedMask {
    private final Menu menu;
    private final String name;
    private final Map<String, Object> section;
    private MaskSlot maskSlot = MaskSlot.of();
    private String currentValue = "0";
    private String maxValue = "100";
    private Button completeButton = Button.EMPTY;
    private Button incompleteButton = Button.EMPTY;

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
    public @NotNull Map<Integer, Button> generateButtons(@NotNull UUID uuid, int size) {
        String parsedCurrentValue = StringReplacerApplier.replace(currentValue, uuid, this);
        String parsedMaxValue = StringReplacerApplier.replace(maxValue, uuid, this);
        List<Integer> slots = maskSlot.getSlots(uuid);

        double current = Validate.getNumber(parsedCurrentValue).map(Number::doubleValue).orElse(0.0);
        double max = Validate.getNumber(parsedMaxValue).map(Number::doubleValue).orElse(100.0);

        int slotsSize = slots.size();
        int completeSize = max <= 0 || current < 0 ? 0 : (int) Math.round(current / max * slotsSize);
        completeSize = Math.min(completeSize, slotsSize);

        Map<Integer, Button> buttonMap = new HashMap<>();
        for (int i = 0; i < completeSize; i++) {
            buttonMap.put(slots.get(i), completeButton);
        }
        for (int i = completeSize; i < slotsSize; i++) {
            buttonMap.put(slots.get(i), incompleteButton);
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
        maskSlot = WrappedMaskSlot.of(section, this);

        completeButton = MapUtils.castOptionalStringObjectMap(MapUtils.getIfFound(section, "complete-button", "complete", "current-button"))
                .flatMap(map -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(menu, name + "_complete_button", map)))
                .map(Button.class::cast)
                .orElse(Button.EMPTY);
        completeButton.init();

        incompleteButton = MapUtils.castOptionalStringObjectMap(MapUtils.getIfFound(section, "incomplete-button", "incomplete", "max-button"))
                .flatMap(map -> ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(menu, name + "_incomplete_button", map)))
                .map(Button.class::cast)
                .orElse(Button.EMPTY);
        incompleteButton.init();
    }

    @Override
    public void stop() {
        if (completeButton != null) {
            completeButton.stop();
        }
        if (incompleteButton != null) {
            incompleteButton.stop();
        }
    }
}
