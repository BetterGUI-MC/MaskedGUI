package me.hsgamer.bettergui.maskedgui.slot;

import me.hsgamer.bettergui.api.menu.MenuElement;
import me.hsgamer.bettergui.util.SlotUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.minecraft.gui.mask.MaskSlot;
import me.hsgamer.hscore.minecraft.gui.mask.MaskUtils;
import me.hsgamer.hscore.minecraft.gui.object.InventoryPosition;
import me.hsgamer.hscore.minecraft.gui.object.InventorySize;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WrappedMaskSlot implements MaskSlot {
    private static final Pattern GRAPH_PATTERN = Pattern.compile("(\\d+)-(\\d+)-(\\d+)-(\\d+)(-[oO])?");
    private static final String POS_SLOT = "slot";
    private static final String POS_DYNAMIC_SLOT = "dynamic-slot";
    private final BiFunction<UUID, InventorySize, List<Integer>> slotFunction;

    public WrappedMaskSlot(BiFunction<UUID, InventorySize, List<Integer>> slotFunction, boolean dynamic) {
        if (dynamic) {
            this.slotFunction = new BiFunction<UUID, InventorySize, List<Integer>>() {
                private final Map<UUID, List<Integer>> cachedSlots = new ConcurrentHashMap<>();

                @Override
                public List<Integer> apply(UUID uuid, InventorySize size) {
                    return cachedSlots.computeIfAbsent(uuid, u -> slotFunction.apply(u, size));
                }
            };
        } else {
            this.slotFunction = new BiFunction<UUID, InventorySize, List<Integer>>() {
                private List<Integer> cachedSlots = null;

                @Override
                public List<Integer> apply(UUID uuid, InventorySize size) {
                    if (cachedSlots == null) {
                        cachedSlots = slotFunction.apply(uuid, size);
                    }
                    return cachedSlots;
                }
            };
        }
    }

    public static WrappedMaskSlot of(Map<String, Object> settings, MenuElement menuElement) {
        BiFunction<UUID, InventorySize, List<Integer>> slotFunction;
        boolean dynamic = false;

        if (settings.containsKey(POS_SLOT)) {
            String slot = Objects.toString(settings.get(POS_SLOT));
            slotFunction = (uuid, size) -> getSlots(slot, size);
        } else if (settings.containsKey(POS_DYNAMIC_SLOT)) {
            String rawSlot = Objects.toString(settings.get(POS_DYNAMIC_SLOT));
            slotFunction = (uuid, size) -> {
                String slot = StringReplacerApplier.replace(rawSlot, uuid, menuElement);
                return getSlots(slot, size);
            };
            dynamic = true;
        } else {
            slotFunction = (uuid, size) -> SlotUtil.getSlots(settings).boxed().collect(Collectors.toList());
        }

        return new WrappedMaskSlot(slotFunction, dynamic);
    }

    private static List<Integer> getSlots(String slot, InventorySize size) {
        Matcher matcher = GRAPH_PATTERN.matcher(slot);
        if (matcher.matches()) {
            int x1 = Math.max(1, Integer.parseInt(matcher.group(1))) - 1;
            int y1 = Math.max(1, Integer.parseInt(matcher.group(2))) - 1;
            int x2 = Math.max(1, Integer.parseInt(matcher.group(3))) - 1;
            int y2 = Math.max(1, Integer.parseInt(matcher.group(4))) - 1;
            boolean outline = matcher.group(5) != null;
            IntStream slotStream;
            if (outline) {
                slotStream = MaskUtils.generateOutlineSlots(InventoryPosition.of(x1, y1), InventoryPosition.of(x2, y2), size);
            } else {
                slotStream = MaskUtils.generateAreaSlots(InventoryPosition.of(x1, y1), InventoryPosition.of(x2, y2), size);
            }
            return slotStream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } else {
            return SlotUtil.generateSlots(slot).boxed().collect(Collectors.toList());
        }
    }

    @Override
    public @NotNull List<Integer> getSlots(UUID uuid, InventorySize size) {
        return slotFunction.apply(uuid, size);
    }
}
