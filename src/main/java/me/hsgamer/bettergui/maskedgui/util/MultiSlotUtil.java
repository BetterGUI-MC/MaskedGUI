package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.hscore.minecraft.gui.mask.MaskUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiSlotUtil {
    private static final Pattern GRAPH_PATTERN = Pattern.compile("(\\d+)-(\\d+)-(\\d+)-(\\d+)(-[oO])?");
    private static final String POS_SLOT = "slot";

    private MultiSlotUtil() {
        // EMPTY
    }

    public static List<Integer> getSlots(Map<String, Object> map) {
        String slot = Optional.ofNullable(map.get(POS_SLOT)).map(Object::toString).orElse("");
        Matcher matcher = GRAPH_PATTERN.matcher(slot);
        if (matcher.matches()) {
            int x1 = Math.max(1, Integer.parseInt(matcher.group(1))) - 1;
            int y1 = Math.max(1, Integer.parseInt(matcher.group(2))) - 1;
            int x2 = Math.max(1, Integer.parseInt(matcher.group(3))) - 1;
            int y2 = Math.max(1, Integer.parseInt(matcher.group(4))) - 1;
            boolean outline = matcher.group(5) != null;
            List<Integer> slots = new ArrayList<>();
            if (outline) {
                MaskUtils.generateOutlineSlots(x1, y1, x2, y2).forEach(slots::add);
            } else {
                MaskUtils.generateAreaSlots(x1, y1, x2, y2).forEach(slots::add);
            }
            return slots;
        } else {
            return SlotUtil.getSlots(map);
        }
    }

}
