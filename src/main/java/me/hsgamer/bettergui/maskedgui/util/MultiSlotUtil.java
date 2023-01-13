package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.hscore.bukkit.gui.mask.MaskUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MultiSlotUtil {
    private static final String POS_X1 = "x1";
    private static final String POS_Y1 = "y1";
    private static final String POS_X2 = "x2";
    private static final String POS_Y2 = "y2";
    private static final String POS_SLOT = "slot";

    private MultiSlotUtil() {
        // EMPTY
    }

    public static List<Integer> getSlots(Map<String, Object> map) {
        if (map.containsKey(POS_X1) && map.containsKey(POS_Y1) && map.containsKey(POS_X2) && map.containsKey(POS_Y2)) {
            int x1 = Integer.parseInt(map.get(POS_X1).toString());
            int y1 = Integer.parseInt(map.get(POS_Y1).toString());
            int x2 = Integer.parseInt(map.get(POS_X2).toString());
            int y2 = Integer.parseInt(map.get(POS_Y2).toString());
            String slot = Optional.ofNullable(map.get(POS_SLOT)).map(Object::toString).orElse("");

            List<Integer> slots = new ArrayList<>();
            if (slot.equalsIgnoreCase("outline")) {
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
