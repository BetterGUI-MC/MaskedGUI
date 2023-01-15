package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.ListMask;

import java.util.Map;
import java.util.UUID;

public class WrappedListMask extends BaseWrappedMask<ListMask> {
    public WrappedListMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ListMask createMask(Map<String, Object> section) {
        ListMask listMask = new ListMask(getName());
        MaskUtil.createChildMasks(this, section).forEach(listMask::addChildMasks);
        return listMask;
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        MaskUtil.refreshMasks(uuid, mask.getMasks());
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        super.handleSignal(uuid, signal);
        MaskUtil.handleSignal(uuid, mask.getMasks(), signal);
    }
}
