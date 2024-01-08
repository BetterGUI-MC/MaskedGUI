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
package me.hsgamer.bettergui.maskedgui.util;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.minecraft.gui.button.Button;

import java.util.*;
import java.util.stream.Collectors;

public final class ButtonUtil {
    private ButtonUtil() {
        // EMPTY
    }

    public static ButtonWithInputMap createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap, String prefix) {
        Map<String, ButtonWithInput> map = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : buttonMap.entrySet()) {
            String name = entry.getKey();
            Optional<Map<String, Object>> optionalValues = MapUtils.castOptionalStringObjectMap(entry.getValue());
            if (!optionalValues.isPresent()) continue;
            Map<String, Object> values = new CaseInsensitiveStringMap<>(optionalValues.get());
            ButtonBuilder.Input input = new ButtonBuilder.Input(wrappedMask.getMenu(), prefix + name, values);
            ButtonBuilder.INSTANCE.build(input).ifPresent(button -> map.put(name, new ButtonWithInput(input, button)));
        }
        return new ButtonWithInputMap(map);
    }

    public static ButtonWithInputMap createButtons(WrappedMask wrappedMask, Map<String, Object> buttonMap) {
        return createButtons(wrappedMask, buttonMap, wrappedMask.getName() + "_button_");
    }

    public static ButtonWithInputMap createChildButtons(WrappedMask mask, Map<String, Object> options) {
        return Optional.ofNullable(MapUtils.getIfFound(options, "button", "buttons", "child"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(o -> createButtons(mask, o))
                .orElseGet(() -> new ButtonWithInputMap(Collections.emptyMap()));
    }

    public static void refreshButtons(UUID uuid, Collection<? extends Button> buttons) {
        buttons.stream()
                .filter(WrappedButton.class::isInstance)
                .map(WrappedButton.class::cast)
                .forEach(button -> button.refresh(uuid));
    }

    public static final class ButtonWithInput {
        public final ButtonBuilder.Input input;
        public final WrappedButton button;

        public ButtonWithInput(ButtonBuilder.Input input, WrappedButton button) {
            this.input = input;
            this.button = button;
        }
    }

    public static final class ButtonWithInputMap {
        public final Map<String, ButtonWithInput> map;

        public ButtonWithInputMap(Map<String, ButtonWithInput> map) {
            this.map = map;
        }

        public Map<String, WrappedButton> buttonMap() {
            return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().button, (a, b) -> b, LinkedHashMap::new));
        }

        public List<WrappedButton> buttonList() {
            return map.values().stream().map(e -> e.button).collect(Collectors.toList());
        }
    }
}
