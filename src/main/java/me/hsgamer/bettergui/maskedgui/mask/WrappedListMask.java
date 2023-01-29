package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.ListMask;

import java.util.Map;
import java.util.UUID;

public class WrappedListMask extends BaseWrappedMask<ListMask> {
    public WrappedListMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ListMask createMask(Map<String, Object> section) {
        return new ListMask(getName()).addMask(MaskUtil.createChildMasks(this, section));
    }

    @Override
    protected void refresh(ListMask mask, UUID uuid) {
        MaskUtil.refreshMasks(uuid, mask.getMasks());
    }

    @Override
    protected void handleSignal(ListMask mask, UUID uuid, Signal signal) {
        MaskUtil.handleSignal(uuid, mask.getMasks(), signal);
    }
}
