package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.BetterGUI;
import me.hsgamer.bettergui.maskedgui.api.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.WrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.mask.impl.AnimatedMask;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.Validate;

import java.math.BigDecimal;
import java.util.*;

public class WrappedAnimatedMask extends BaseWrappedMask<AnimatedMask> {
    public WrappedAnimatedMask(MaskBuilder.Input input) {
        super(input);
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

        AnimatedMask animatedMask = new AnimatedMask(getName(), BetterGUI.getInstance(), update, async);
        frames.forEach(animatedMask::addChildMasks);
        return animatedMask;
    }

    @Override
    public void refresh(UUID uuid) {
        mask.getMasks().stream()
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .forEach(wrappedMask -> wrappedMask.refresh(uuid));
    }
}
