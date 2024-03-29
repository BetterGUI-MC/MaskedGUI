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
package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.minecraft.gui.mask.Mask;

import java.util.*;

public final class MaskUtil {
    private MaskUtil() {
        // EMPTY
    }

    public static void refreshMasks(UUID uuid, Collection<? extends Mask> masks) {
        masks.stream()
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .forEach(mask -> mask.refresh(uuid));
    }

    public static void handleSignal(UUID uuid, Collection<? extends Mask> masks, Signal signal) {
        masks.stream()
                .filter(WrappedMask.class::isInstance)
                .map(WrappedMask.class::cast)
                .forEach(mask -> mask.handleSignal(uuid, signal));
    }

    public static Map<String, WrappedMask> createChildMasks(WrappedMask mask, Map<String, Object> options) {
        return Optional.ofNullable(MapUtils.getIfFound(options, "masks", "child"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(o -> MaskBuilder.INSTANCE.getChildMasksAsMap(mask, o))
                .orElseGet(Collections::emptyMap);
    }

    public static List<WrappedMask> createChildMasksAsList(WrappedMask mask, Map<String, Object> options) {
        return new ArrayList<>(createChildMasks(mask, options).values());
    }
}
