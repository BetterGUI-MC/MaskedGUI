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
package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.minecraft.gui.mask.MaskSlot;
import me.hsgamer.hscore.minecraft.gui.mask.MaskUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultiSlotUtil {
    private static final Pattern GRAPH_PATTERN = Pattern.compile("(\\d+)-(\\d+)-(\\d+)-(\\d+)(-[oO])?");
    private static final String POS_SLOT = "slot";
    private static final String POS_DYNAMIC_SLOT = "dynamic-slot";
    private static final Map<String, List<Integer>> cachedSlots = new ConcurrentHashMap<>();

    private MultiSlotUtil() {
        // EMPTY
    }

    public static List<Integer> getSlots(String slot) {
        return cachedSlots.computeIfAbsent(slot, s -> {
            Matcher matcher = GRAPH_PATTERN.matcher(s);
            if (matcher.matches()) {
                int x1 = Math.max(1, Integer.parseInt(matcher.group(1))) - 1;
                int y1 = Math.max(1, Integer.parseInt(matcher.group(2))) - 1;
                int x2 = Math.max(1, Integer.parseInt(matcher.group(3))) - 1;
                int y2 = Math.max(1, Integer.parseInt(matcher.group(4))) - 1;
                boolean outline = matcher.group(5) != null;
                IntStream slotStream;
                if (outline) {
                    slotStream = MaskUtils.generateOutlineSlots(x1, y1, x2, y2);
                } else {
                    slotStream = MaskUtils.generateAreaSlots(x1, y1, x2, y2);
                }
                return slotStream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            } else {
                return SlotUtil.generateSlots(s).collect(Collectors.toList());
            }
        });
    }

    public static MaskSlot getMaskSlot(Map<String, Object> map, MenuElement menuElement) {
        Optional<String> optionalDynamicSlot = Optional.ofNullable(map.get(POS_DYNAMIC_SLOT)).map(Object::toString);
        if (optionalDynamicSlot.isPresent()) {
            String rawSlot = optionalDynamicSlot.get();
            return uuid -> {
                String slot = StringReplacerApplier.replace(rawSlot, uuid, menuElement);
                return getSlots(slot);
            };
        }

        List<Integer> slots = Optional.ofNullable(map.get(POS_SLOT))
                .map(Object::toString)
                .map(MultiSlotUtil::getSlots)
                .orElseGet(() -> SlotUtil.getSlots(map));
        return uuid -> slots;
    }
}
