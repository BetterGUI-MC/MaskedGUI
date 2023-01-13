package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.StaticMaskPaginatedMask;

import java.util.*;

public class WrappedMaskPaginatedMask extends WrappedPaginatedMask<StaticMaskPaginatedMask> {
    public WrappedMaskPaginatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected StaticMaskPaginatedMask createPaginatedMask(Map<String, Object> section) {
        List<WrappedMask> masks = Optional.ofNullable(section.get("mask"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .map(o -> MaskBuilder.INSTANCE.getChildMasks(this, o))
                .orElse(Collections.emptyList());
        StaticMaskPaginatedMask animatedMask = new StaticMaskPaginatedMask(getName());
        masks.forEach(animatedMask::addMasks);
        return animatedMask;
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        MaskUtil.refreshMasks(uuid, mask.getMasks(uuid));
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        super.handleSignal(uuid, signal);
        MaskUtil.handleSignal(uuid, mask.getMasks(uuid), signal);
    }
}
