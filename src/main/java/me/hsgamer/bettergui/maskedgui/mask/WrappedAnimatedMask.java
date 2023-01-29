package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.mask.impl.AnimatedMask;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WrappedAnimatedMask extends BaseWrappedMask<AnimatedMask> {
    public WrappedAnimatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected AnimatedMask createMask(Map<String, Object> section) {
        long update = Optional.ofNullable(section.get("update"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
                .map(BigDecimal::longValue)
                .orElse(0L);

        List<WrappedMask> frames = MaskUtil.createChildMasks(this, section);
        return new AnimatedMask(getName()).addMask(frames).setPeriodTicks(update);
    }

    @Override
    protected void refresh(AnimatedMask mask, UUID uuid) {
        MaskUtil.refreshMasks(uuid, mask.getMasks());
    }

    @Override
    protected void handleSignal(AnimatedMask mask, UUID uuid, Signal signal) {
        MaskUtil.handleSignal(uuid, mask.getMasks(), signal);
    }
}
