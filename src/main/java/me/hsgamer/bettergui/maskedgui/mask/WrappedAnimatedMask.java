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

        List<WrappedMask> frames = MaskUtil.createChildMasksAsList(this, section);
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
