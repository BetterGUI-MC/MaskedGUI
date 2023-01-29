package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.StaticButtonPaginatedMask;

import java.util.Map;
import java.util.UUID;

public class WrappedButtonPaginatedMask extends WrappedPaginatedMask<StaticButtonPaginatedMask> {
    public WrappedButtonPaginatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected StaticButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        return new StaticButtonPaginatedMask(getName(), MultiSlotUtil.getSlots(section))
                .addButton(MaskUtil.createChildButtons(this, section).values());
    }

    @Override
    protected void refresh(StaticButtonPaginatedMask mask, UUID uuid) {
        MaskUtil.refreshButtons(uuid, mask.getButtons(uuid));
    }
}
