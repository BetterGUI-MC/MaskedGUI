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
package me.hsgamer.bettergui.maskedgui.menu;

import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MaskUtil;
import me.hsgamer.bettergui.menu.BaseInventoryMenu;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.minecraft.gui.advanced.AdvancedButtonMap;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MaskedMenu extends BaseInventoryMenu<AdvancedButtonMap> {
    public MaskedMenu(Config config) {
        super(config);
    }

    @Override
    protected AdvancedButtonMap createButtonMap() {
        AdvancedButtonMap buttonMap = new AdvancedButtonMap();
        for (Map.Entry<String, Object> entry : configSettings.entrySet()) {
            String key = entry.getKey();
            Optional<Map<String, Object>> optionalValue = MapUtils.castOptionalStringObjectMap(entry.getValue());
            if (!optionalValue.isPresent()) continue;
            Map<String, Object> value = optionalValue.get();
            Map<String, Object> values = new CaseInsensitiveStringMap<>(value);
            MaskBuilder.INSTANCE
                    .build(new MaskBuilder.Input(this, "mask_" + key, values))
                    .ifPresent(mask -> {
                        mask.init();
                        buttonMap.addMask(mask);
                    });
        }
        return buttonMap;
    }

    @Override
    protected void refreshButtonMapOnCreate(AdvancedButtonMap buttonMap, UUID uuid) {
        MaskUtil.refreshMasks(uuid, buttonMap.getMasks());
    }

    public void handleSignal(UUID uuid, Signal signal) {
        AdvancedButtonMap buttonMap = getButtonMap();
        if (buttonMap == null) return;
        MaskUtil.handleSignal(uuid, buttonMap.getMasks(), signal);
    }
}
