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

import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;

import java.util.Map;
import java.util.UUID;

public class TemplateMask extends BaseWrappedMask<WrappedMask> {
    private final MaskedGUI addon;

    public TemplateMask(MaskedGUI addon, MaskBuilder.Input input) {
        super(input);
        this.addon = addon;
    }

    @Override
    protected WrappedMask createMask(Map<String, Object> section) {
        Map<String, Object> finalMap = addon.getTemplateMaskConfig().getValues(section, "mask");
        return MaskBuilder.INSTANCE.build(new MaskBuilder.Input(getMenu(), getName(), finalMap)).orElse(null);
    }

    @Override
    protected void refresh(WrappedMask mask, UUID uuid) {
        mask.refresh(uuid);
    }

    @Override
    protected void handleSignal(WrappedMask mask, UUID uuid, Signal signal) {
        mask.handleSignal(uuid, signal);
    }
}
