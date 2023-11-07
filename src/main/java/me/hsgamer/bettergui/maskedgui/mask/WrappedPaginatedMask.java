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
import me.hsgamer.bettergui.maskedgui.signal.NextPageSignal;
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import me.hsgamer.bettergui.maskedgui.signal.SetPageSignal;
import me.hsgamer.bettergui.maskedgui.util.SignalHandler;
import me.hsgamer.hscore.common.StringReplacer;
import me.hsgamer.hscore.minecraft.gui.mask.impl.PaginatedMask;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class WrappedPaginatedMask<T extends PaginatedMask> extends BaseWrappedMask<T> {
    protected final SignalHandler signalHandler = new SignalHandler();

    protected WrappedPaginatedMask(MaskBuilder.Input input) {
        super(input);
        input.menu.getVariableManager().register(getName() + "_page", StringReplacer.of((original, uuid) -> {
            if (getMask() == null) return null;
            return Integer.toString(getMask().getPage(uuid) + 1);
        }));
        input.menu.getVariableManager().register(getName() + "_max", StringReplacer.of((original, uuid) -> {
            if (getMask() == null) return null;
            return Integer.toString(getMask().getPageAmount(uuid));
        }));
    }

    protected abstract T createPaginatedMask(Map<String, Object> section);

    @Override
    protected T createMask(Map<String, Object> section) {
        T mask = createPaginatedMask(section);
        Optional.ofNullable(section.get("cycle")).map(Object::toString).map(Boolean::parseBoolean).ifPresent(mask::setCycle);
        signalHandler
                .setSignal(section, getName())
                .addHandler(NextPageSignal.class, (uuid, changePageSignal) -> {
                    if (changePageSignal.isNext()) {
                        mask.nextPage(uuid);
                    } else {
                        mask.previousPage(uuid);
                    }
                })
                .addHandler(RefreshMaskSignal.class, (uuid, refreshMaskSignal) -> mask.setPage(uuid, 0))
                .addHandler(SetPageSignal.class, (uuid, setPageSignal) -> mask.setPage(uuid, setPageSignal.getPage()));
        return mask;
    }

    @Override
    protected void handleSignal(T mask, UUID uuid, Signal signal) {
        signalHandler.handle(uuid, signal);
    }

    @Override
    public void stop() {
        signalHandler.clear();
        super.stop();
    }
}
