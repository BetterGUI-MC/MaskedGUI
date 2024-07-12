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

import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.AnimatedMask;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WrappedAnimatedMask extends BaseWrappedMask<AnimatedMask> {
    public WrappedAnimatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected AnimatedMask createMask(Map<String, Object> section) {
        AnimatedMask mask = new AnimatedMask(getName());
        mask.addMask(MaskUtil.createChildMasksAsList(this, section));

        Optional.ofNullable(section.get("update"))
                .map(String::valueOf)
                .flatMap(TickUtil::toMillis)
                .filter(n -> n > 0)
                .ifPresent(mask::setPeriodMillis);

        return mask;
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
