package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.bukkit.gui.button.Button;
import me.hsgamer.hscore.bukkit.gui.mask.Mask;

import java.util.*;

public class HybridMask implements WrappedMask {
    private final MaskBuilder.Input input;
    private final List<Mask> masks = new ArrayList<>();

    public HybridMask(MaskBuilder.Input input) {
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public Map<Integer, Button> generateButtons(UUID uuid) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        masks.forEach(mask -> buttonMap.putAll(mask.generateButtons(uuid)));
        return buttonMap;
    }

    @Override
    public String getName() {
        return input.name;
    }

    @Override
    public void refresh(UUID uuid) {
        MaskUtil.refreshMasks(uuid, masks);
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        MaskUtil.handleSignal(uuid, masks, signal);
    }

    @Override
    public void init() {
        Optional.ofNullable(input.options.get("child"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .map(o -> MaskBuilder.INSTANCE.getChildMasks(this, o))
                .ifPresent(masks::addAll);
        masks.forEach(Mask::init);
    }

    @Override
    public void stop() {
        masks.forEach(Mask::stop);
        masks.clear();
    }
}
