/*
   Copyright 2023-2024 Huynh Tien

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
package me.hsgamer.bettergui.maskedgui.util;

import io.github.projectunified.craftux.common.Position;
import io.github.projectunified.craftux.mask.MaskUtils;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.maskedgui.menu.BaseMaskedMenu;
import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.object.InventoryPosition;
import me.hsgamer.hscore.minecraft.gui.object.InventorySize;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MaskSlotUtil {
    private static final Pattern GRAPH_PATTERN = Pattern.compile("(\\d+)-(\\d+)-(\\d+)-(\\d+)(-[oO])?");
    private static final String POS_X = "position-x";
    private static final String POS_Y = "position-y";
    private static final String POS_SLOT = "slot";
    private static final String POS_DYNAMIC_SLOT = "dynamic-slot";

    public static Function<UUID, List<Position>> of(Map<String, Object> settings, Menu menu) {
        InventorySize inventorySize = ((BaseMaskedMenu) menu).makeFakeInventorySize();
        List<Function<UUID, List<Position>>> slotFunctions = new ArrayList<>();
        if (settings.containsKey(POS_X) && settings.containsKey(POS_Y)) {
            Optional<Integer> x = Validate.getNumber(String.valueOf(settings.get(POS_X))).map(BigDecimal::intValue);
            Optional<Integer> y = Validate.getNumber(String.valueOf(settings.get(POS_Y))).map(BigDecimal::intValue);
            if (x.isPresent() && y.isPresent()) {
                List<Position> positions = Collections.singletonList(Position.of(x.get(), y.get()));
                slotFunctions.add(uuid -> positions);
            }
        }
        if (settings.containsKey(POS_SLOT)) {
            String slot = Objects.toString(settings.get(POS_SLOT));
            List<Position> positions = getSlots(slot, inventorySize);
            slotFunctions.add(uuid -> positions);
        }
        if (settings.containsKey(POS_DYNAMIC_SLOT)) {
            String rawSlot = Objects.toString(settings.get(POS_DYNAMIC_SLOT));
            slotFunctions.add(uuid -> {
                String slot = StringReplacerApplier.replace(rawSlot, uuid, menu);
                return getSlots(slot, inventorySize);
            });
        }
        if (slotFunctions.isEmpty()) {
            return uuid -> Collections.emptyList();
        } else {
            return uuid -> {
                List<Position> positions = new ArrayList<>();
                for (Function<UUID, List<Position>> slotFunction : slotFunctions) {
                    positions.addAll(slotFunction.apply(uuid));
                }
                return positions;
            };
        }
    }

    public static Function<UUID, List<Position>> of(Map<String, Object> settings, MenuElement menuElement) {
        return of(settings, menuElement.getMenu());
    }

    private static List<Position> getSlots(String slot, InventorySize size) {
        Matcher matcher = GRAPH_PATTERN.matcher(slot);
        if (matcher.matches()) {
            int x1 = Math.max(1, Integer.parseInt(matcher.group(1))) - 1;
            int y1 = Math.max(1, Integer.parseInt(matcher.group(2))) - 1;
            int x2 = Math.max(1, Integer.parseInt(matcher.group(3))) - 1;
            int y2 = Math.max(1, Integer.parseInt(matcher.group(4))) - 1;
            Position position1 = Position.of(x1, y1);
            Position position2 = Position.of(x2, y2);
            boolean outline = matcher.group(5) != null;
            if (outline) {
                return MaskUtils.generateOutlinePositions(position1, position2);
            } else {
                return MaskUtils.generateAreaPositions(position1, position2);
            }
        } else {
            return SlotUtil.generateSlots(slot).mapToObj(i -> {
                InventoryPosition inventoryPosition = size.toPosition(i);
                return Position.of(inventoryPosition.getX(), inventoryPosition.getY());
            }).collect(Collectors.toList());
        }
    }
}
