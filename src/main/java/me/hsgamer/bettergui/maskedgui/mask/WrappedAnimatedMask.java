package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.bettergui.maskedgui.api.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.AnimatedMask;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;

public class WrappedAnimatedMask extends BaseWrappedMask<AnimatedMask> {
    private final MaskedGUI addon;

    public WrappedAnimatedMask(MaskedGUI addon, MaskBuilder.Input input) {
        super(input);
        this.addon = addon;
    }

    @Override
    protected AnimatedMask createMask(Map<String, Object> section) {
        Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
        long update = Optional.ofNullable(keys.get("update"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
                .map(BigDecimal::longValue)
                .orElse(0L);
        boolean async = Optional.ofNullable(keys.get("async"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(true);

        List<WrappedMask> frames = Optional.ofNullable(keys.get("child"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .map(o -> MaskBuilder.INSTANCE.getChildMasks(this, o))
                .orElse(Collections.emptyList());

        AnimatedMask animatedMask = new AnimatedMask(getName(), addon.getPlugin(), update, async);
        frames.forEach(animatedMask::addChildMasks);
        return animatedMask;
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask == null) return;
        MaskUtil.refreshMasks(uuid, mask.getMasks());
    }
}
