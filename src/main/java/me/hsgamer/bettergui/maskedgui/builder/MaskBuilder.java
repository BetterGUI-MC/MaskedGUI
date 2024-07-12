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
package me.hsgamer.bettergui.maskedgui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.hscore.builder.FunctionalMassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringLinkedMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MaskBuilder extends FunctionalMassBuilder<MaskBuilder.Input, WrappedMask> {
    public static final MaskBuilder INSTANCE = new MaskBuilder();
    private String defaultMaskType = "";

    private MaskBuilder() {
        super();
    }

    @Override
    protected String getType(Input input) {
        return Objects.toString(new CaseInsensitiveStringMap<>(input.options).get("mask"), defaultMaskType);
    }

    public void setDefaultMaskType(String defaultMaskType) {
        this.defaultMaskType = defaultMaskType;
    }

    public Map<String, WrappedMask> getChildMasksAsMap(WrappedMask parentMask, Map<String, Object> section) {
        return section.entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .map(entry -> {
                    // noinspection unchecked
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                    String name = parentMask.getName() + "_child_" + entry.getKey();
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), new Input(parentMask.getMenu(), name, value));
                })
                .flatMap(entry -> {
                    WrappedMask mask = build(entry.getValue()).orElse(null);
                    if (mask == null) {
                        return Stream.empty();
                    }
                    return Stream.of(new AbstractMap.SimpleEntry<>(entry.getKey(), mask));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, CaseInsensitiveStringLinkedMap::new));
    }

    public static class Input {
        public final Menu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(Menu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
