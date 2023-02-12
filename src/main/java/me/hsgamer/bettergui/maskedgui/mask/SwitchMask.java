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
import me.hsgamer.bettergui.maskedgui.signal.RefreshMaskSignal;
import me.hsgamer.bettergui.maskedgui.signal.SetMaskSignal;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.SignalHandler;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.PlaceholderMask;
import me.hsgamer.hscore.ui.property.Initializable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SwitchMask extends BaseWrappedMask<PlaceholderMask> {
    protected final SignalHandler signalHandler = new SignalHandler();
    private Map<String, WrappedMask> childMasks = Collections.emptyMap();

    public SwitchMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected PlaceholderMask createMask(Map<String, Object> section) {
        childMasks = MaskUtil.createChildMasks(this, section);

        PlaceholderMask mask = new PlaceholderMask(getName()).setInitDefaultMask(false);
        Optional.ofNullable(MapUtil.getIfFound(section, "default", "default-mask"))
                .map(String::valueOf)
                .map(childMasks::get)
                .ifPresent(mask::setDefaultMask);

        signalHandler
                .setSignal(section, getName())
                .addHandler(SetMaskSignal.class, (uuid, setMaskSignal) -> {
                    WrappedMask childMask = childMasks.get(setMaskSignal.getMaskName());
                    if (childMask != null) {
                        mask.setMask(uuid, childMask);
                    }
                })
                .addHandler(RefreshMaskSignal.class, (uuid, refreshMaskSignal) -> mask.setMask(uuid, null));

        return mask;
    }

    @Override
    public void init() {
        super.init();
        childMasks.values().forEach(Initializable::init);
    }

    @Override
    public void stop() {
        super.stop();
        childMasks.values().forEach(Initializable::stop);
    }

    @Override
    protected void refresh(PlaceholderMask mask, UUID uuid) {
        MaskUtil.refreshMasks(uuid, childMasks.values());
    }

    @Override
    protected void handleSignal(PlaceholderMask mask, UUID uuid, Signal signal) {
        signalHandler.handle(uuid, signal);
        MaskUtil.handleSignal(uuid, childMasks.values(), signal);
    }
}
