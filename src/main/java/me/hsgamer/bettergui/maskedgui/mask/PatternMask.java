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

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.minecraft.gui.mask.MaskUtils;
import me.hsgamer.hscore.minecraft.gui.mask.impl.ButtonMapMask;

import java.util.*;

public class PatternMask extends BaseWrappedMask<ButtonMapMask> {
    public PatternMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ButtonMapMask createMask(Map<String, Object> section) {
        List<String> pattern = CollectionUtils.createStringListFromObject(section.get("pattern"));
        if (pattern.isEmpty()) return null;
        Map<Character, List<Integer>> patternMap = new HashMap<>();
        for (int y = 0; y < pattern.size(); y++) {
            String line = pattern.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                c = c == '.' ? ' ' : c;
                patternMap.computeIfAbsent(c, k -> new ArrayList<>()).add(MaskUtils.toSlot(x, y));
            }
        }

        Map<String, WrappedButton> buttonElements = ButtonUtil.createChildButtons(this, section).buttonMap();

        ButtonMapMask mask = new ButtonMapMask(getName());
        for (Map.Entry<String, WrappedButton> entry : buttonElements.entrySet()) {
            String keyString = entry.getKey();
            char key = keyString.isEmpty() ? ' ' : keyString.charAt(0);
            List<Integer> slots = patternMap.get(key);
            if (slots != null) {
                mask.addButton(entry.getValue(), slots);
            }
        }
        return mask;
    }

    @Override
    protected void refresh(ButtonMapMask mask, UUID uuid) {
        ButtonUtil.refreshButtons(uuid, mask.getButtons());
    }
}
