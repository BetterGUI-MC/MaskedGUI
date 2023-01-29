package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.MultiSlotsMask;

import java.util.Map;
import java.util.UUID;

public class WrappedMultiSlotMasks extends BaseWrappedMask<MultiSlotsMask> {
    public WrappedMultiSlotMasks(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected MultiSlotsMask createMask(Map<String, Object> section) {
        return new MultiSlotsMask(getName(), MultiSlotUtil.getSlots(section)).addButton(MaskUtil.createChildButtons(this, section).values());
    }

    @Override
    protected void refresh(MultiSlotsMask mask, UUID uuid) {
        MaskUtil.refreshButtons(uuid, mask.getButtons());
    }
}
