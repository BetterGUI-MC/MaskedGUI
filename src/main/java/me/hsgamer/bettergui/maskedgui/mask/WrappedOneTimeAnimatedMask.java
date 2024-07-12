package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.SignalHandler;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.mask.impl.OneTimeAnimatedMask;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WrappedOneTimeAnimatedMask extends BaseWrappedMask<OneTimeAnimatedMask> {
    private final SignalHandler signalHandler = new SignalHandler();

    public WrappedOneTimeAnimatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected OneTimeAnimatedMask createMask(Map<String, Object> section) {
        OneTimeAnimatedMask mask = new OneTimeAnimatedMask(getName());
        mask.addMask(MaskUtil.createChildMasksAsList(this, section));

        Optional.ofNullable(section.get("update"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
                .map(BigDecimal::longValue)
                .ifPresent(mask::setPeriodTicks);
        Optional.ofNullable(MapUtils.getIfFound(section, "view-last", "keep-last", "last"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .ifPresent(mask::setViewLast);

        signalHandler
                .setSignal(section, getName())
                .addHandler(RefreshMaskSignal.class, (uuid, refreshMaskSignal) -> this.refresh(uuid));

        return mask;
    }

    @Override
    protected void refresh(OneTimeAnimatedMask mask, UUID uuid) {
        mask.reset(uuid);
        MaskUtil.refreshMasks(uuid, mask.getMasks());
    }

    @Override
    protected void handleSignal(OneTimeAnimatedMask mask, UUID uuid, Signal signal) {
        signalHandler.handle(uuid, signal);
        MaskUtil.handleSignal(uuid, mask.getMasks(), signal);
    }

    @Override
    public void stop() {
        signalHandler.clear();
        super.stop();
    }
}
