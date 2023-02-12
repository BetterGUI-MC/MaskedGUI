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

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.hscore.minecraft.gui.mask.impl.StaticButtonPaginatedMask;

import java.util.Map;
import java.util.UUID;

public class WrappedButtonPaginatedMask extends WrappedPaginatedMask<StaticButtonPaginatedMask> {
    public WrappedButtonPaginatedMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected StaticButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        return new StaticButtonPaginatedMask(getName(), MultiSlotUtil.getSlots(section))
                .addButton(MaskUtil.createChildButtons(this, section).values());
    }

    @Override
    protected void refresh(StaticButtonPaginatedMask mask, UUID uuid) {
        MaskUtil.refreshButtons(uuid, mask.getButtons(uuid));
    }
}
