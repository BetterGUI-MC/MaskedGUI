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
import java.util.concurrent.ConcurrentSkipListSet;

public class HybridMask implements WrappedMask {
    private final MaskBuilder.Input input;
    private final LinkedHashMap<Mask, Set<UUID>> maskMap = new LinkedHashMap<>();

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
        for (Map.Entry<Mask, Set<UUID>> entry : maskMap.entrySet()) {
            if (entry.getValue().contains(uuid)) {
                buttonMap.putAll(entry.getKey().generateButtons(uuid));
            }
        }
        return buttonMap;
    }

    @Override
    public boolean canView(UUID uuid) {
        boolean canView = false;
        for (Map.Entry<Mask, Set<UUID>> entry : maskMap.entrySet()) {
            Set<UUID> uuidSet = entry.getValue();
            if (entry.getKey().canView(uuid)) {
                uuidSet.add(uuid);
                canView = true;
            } else {
                uuidSet.remove(uuid);
            }
        }
        return canView;
    }

    @Override
    public String getName() {
        return input.name;
    }

    @Override
    public void refresh(UUID uuid) {
        MaskUtil.refreshMasks(uuid, maskMap.keySet());
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        MaskUtil.handleSignal(uuid, maskMap.keySet(), signal);
    }

    @Override
    public void init() {
        Optional.ofNullable(input.options.get("child"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .map(o -> MaskBuilder.INSTANCE.getChildMasks(this, o))
                .ifPresent(childMarks -> childMarks.forEach(mask -> maskMap.put(mask, new ConcurrentSkipListSet<>())));
        maskMap.keySet().forEach(Mask::init);
    }

    @Override
    public void stop() {
        maskMap.keySet().forEach(Mask::stop);
        maskMap.clear();
    }
}
