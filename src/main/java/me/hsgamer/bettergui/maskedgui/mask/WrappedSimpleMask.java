package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.MultiSlotsMask;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WrappedSimpleMask extends BaseWrappedMask<MultiSlotsMask> {
    public WrappedSimpleMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected MultiSlotsMask createMask(Map<String, Object> section) {
        List<Integer> slots = MultiSlotUtil.getSlots(section);
        MultiSlotsMask mask = new MultiSlotsMask(getName(), slots);
        ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_button", section)).ifPresent(mask::addButton);
        return mask;
    }

    @Override
    protected void refresh(MultiSlotsMask mask, UUID uuid) {
        MaskUtil.refreshButtons(uuid, mask.getButtons());
    }
}
