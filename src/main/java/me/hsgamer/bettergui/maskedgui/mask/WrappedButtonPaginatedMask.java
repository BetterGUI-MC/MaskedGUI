package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.StaticButtonPaginatedMask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrappedButtonPaginatedMask extends WrappedPaginatedMask<StaticButtonPaginatedMask> {
    public WrappedButtonPaginatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected StaticButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        List<Integer> slots = MultiSlotUtil.getSlots(section);
        StaticButtonPaginatedMask mask = new StaticButtonPaginatedMask(getName(), slots);
        MaskUtil.createChildButtons(this, section).forEach((key, value) -> mask.addButtons(value));
        return mask;
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        MaskUtil.refreshButtons(uuid, mask.getButtons(uuid));
    }
}
