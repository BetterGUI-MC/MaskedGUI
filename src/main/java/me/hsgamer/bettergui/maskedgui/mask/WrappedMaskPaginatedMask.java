package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.StaticMaskPaginatedMask;

import java.util.Map;
import java.util.UUID;

public class WrappedMaskPaginatedMask extends WrappedPaginatedMask<StaticMaskPaginatedMask> {
    public WrappedMaskPaginatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected StaticMaskPaginatedMask createPaginatedMask(Map<String, Object> section) {
        return new StaticMaskPaginatedMask(getName()).addMask(MaskUtil.createChildMasks(this, section));
    }

    @Override
    protected void refresh(StaticMaskPaginatedMask mask, UUID uuid) {
        MaskUtil.refreshMasks(uuid, mask.getMasks(uuid));
    }

    @Override
    protected void handleSignal(StaticMaskPaginatedMask mask, UUID uuid, Signal signal) {
        super.handleSignal(mask, uuid, signal);
        MaskUtil.handleSignal(uuid, mask.getMasks(uuid), signal);
    }
}
