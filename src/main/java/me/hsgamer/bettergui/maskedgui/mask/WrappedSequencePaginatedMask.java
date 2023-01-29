package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.StaticSequencePaginatedMask;

import java.util.Map;
import java.util.UUID;

public class WrappedSequencePaginatedMask extends WrappedPaginatedMask<StaticSequencePaginatedMask> {
    public WrappedSequencePaginatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected StaticSequencePaginatedMask createPaginatedMask(Map<String, Object> section) {
        return new StaticSequencePaginatedMask(getName(), MultiSlotUtil.getSlots(section)).addButton(MaskUtil.createChildButtons(this, section).values());
    }

    @Override
    protected void refresh(StaticSequencePaginatedMask mask, UUID uuid) {
        MaskUtil.refreshButtons(uuid, mask.getButtons(uuid));
    }
}
