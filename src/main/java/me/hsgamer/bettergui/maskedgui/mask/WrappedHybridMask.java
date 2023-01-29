package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.HybridMask;

import java.util.Map;
import java.util.UUID;

public class WrappedHybridMask extends BaseWrappedMask<HybridMask> {
    public WrappedHybridMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected HybridMask createMask(Map<String, Object> section) {
        return new HybridMask(getName()).addMask(MaskUtil.createChildMasks(this, section));
    }

    @Override
    protected void handleSignal(HybridMask mask, UUID uuid, Signal signal) {
        MaskUtil.handleSignal(uuid, mask.getMasks(), signal);
    }

    @Override
    protected void refresh(HybridMask mask, UUID uuid) {
        MaskUtil.refreshMasks(uuid, mask.getMasks());
    }
}
