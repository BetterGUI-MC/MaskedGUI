/*
   Copyright 2023-2023 Huynh Tien

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.SignalHandler;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.mask.Mask;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OneTimeAnimatedMask implements WrappedMask {
    private final MaskedGUI addon;
    private final MaskBuilder.Input input;
    private final List<Mask> masks = new ArrayList<>();
    private final Map<UUID, SequenceRunner> runnerMap = new ConcurrentHashMap<>();
    private final SignalHandler signalHandler = new SignalHandler();
    private long update = 0;
    private boolean async = true;
    private boolean viewLast = false;

    public OneTimeAnimatedMask(MaskedGUI addon, MaskBuilder.Input input) {
        this.addon = addon;
        this.input = input;
    }

    @Override
    public Menu getMenu() {
        return input.menu;
    }

    @Override
    public String getName() {
        return input.name;
    }

    @Override
    public boolean canView(UUID uuid) {
        if (masks.isEmpty()) return false;
        SequenceRunner runner = runnerMap.computeIfAbsent(uuid, k -> {
            SequenceRunner newRunner = new SequenceRunner(masks.size());
            if (async) {
                newRunner.runTaskTimerAsynchronously(addon.getPlugin(), update, update);
            } else {
                newRunner.runTaskTimer(addon.getPlugin(), update, update);
            }
            return newRunner;
        });

        int index = runner.index;
        if (index < 0 || index >= masks.size()) {
            return false;
        }
        if (runner.maxed && !viewLast) {
            return false;
        }
        return masks.get(index).canView(uuid);
    }

    @Override
    public Map<Integer, Button> generateButtons(UUID uuid) {
        if (masks.isEmpty()) return Collections.emptyMap();
        SequenceRunner runner = runnerMap.get(uuid);
        if (runner == null) {
            return Collections.emptyMap();
        }
        int index = runner.index;
        if (index < 0 || index >= masks.size()) {
            return Collections.emptyMap();
        }
        return masks.get(index).generateButtons(uuid);
    }

    private void reset(UUID uuid) {
        Optional.ofNullable(runnerMap.remove(uuid)).ifPresent(SequenceRunner::cancel);
    }

    @Override
    public void refresh(UUID uuid) {
        reset(uuid);
        MaskUtil.refreshMasks(uuid, masks);
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        signalHandler.handle(uuid, signal);
        MaskUtil.handleSignal(uuid, masks, signal);
    }

    @Override
    public void init() {
        masks.addAll(MaskUtil.createChildMasksAsList(this, input.options));
        update = Optional.ofNullable(input.options.get("update"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
                .map(BigDecimal::longValue)
                .orElse(update);
        async = Optional.ofNullable(input.options.get("async"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(async);
        viewLast = Optional.ofNullable(MapUtil.getIfFound(input.options, "view-last", "keep-last", "last"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(viewLast);

        signalHandler
                .setSignal(input.options, getName())
                .addHandler(RefreshMaskSignal.class, (uuid, refreshMaskSignal) -> this.refresh(uuid));
        masks.forEach(Mask::init);
    }

    @Override
    public void stop() {
        signalHandler.clear();
        runnerMap.values().forEach(SequenceRunner::stop);
        runnerMap.clear();
        masks.forEach(Mask::stop);
        masks.clear();
    }

    private static class SequenceRunner extends BukkitRunnable {
        private final int max;
        private int index = 0;
        private boolean maxed = false;

        private SequenceRunner(int max) {
            this.max = max;
        }

        @Override
        public void run() {
            if (maxed) return;
            if (index < max - 1) {
                index++;
            } else {
                maxed = true;
                cancel();
            }
        }

        public void stop() {
            cancel();
        }
    }
}
