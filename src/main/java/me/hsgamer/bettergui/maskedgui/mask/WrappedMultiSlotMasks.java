package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.MultiSlotsMask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrappedMultiSlotMasks extends BaseWrappedMask<MultiSlotsMask> {
    public WrappedMultiSlotMasks(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected MultiSlotsMask createMask(Map<String, Object> section) {
        List<Integer> slots = MultiSlotUtil.getSlots(section);
        MultiSlotsMask mask = new MultiSlotsMask(getName(), slots);
        MaskUtil.createChildButtons(this, section).forEach((key, value) -> mask.addButtons(value));
        return mask;
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        MaskUtil.refreshButtons(uuid, mask.getButtons());
    }
}
