/*
   Copyright 2023-2024 Huynh Tien

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

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.SignalHandler;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.minecraft.gui.mask.impl.OneTimeAnimatedMask;

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
                .flatMap(TickUtil::toMillis)
                .filter(n -> n > 0)
                .ifPresent(mask::setPeriodMillis);
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
