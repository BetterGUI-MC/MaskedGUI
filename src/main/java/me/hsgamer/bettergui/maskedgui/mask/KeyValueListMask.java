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
package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.replacer.SimpleVariableValueReplacer;
import me.hsgamer.bettergui.maskedgui.replacer.ValueReplacer;
import me.hsgamer.hscore.common.MapUtils;

import java.util.*;
import java.util.stream.Stream;

public class KeyValueListMask extends ValueListMask<Map<String, String>> {
    private List<Map<String, String>> valueList = Collections.emptyList();

    public KeyValueListMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ValueReplacer<Map<String, String>> createValueReplacer() {
        return new SimpleVariableValueReplacer<Map<String, String>>() {
            @Override
            protected String getPrefix() {
                return "key";
            }

            @Override
            protected String replaceVariable(String argument, Map<String, String> value) {
                return value.getOrDefault(argument, "");
            }
        };
    }

    @Override
    protected Stream<Map<String, String>> getValueStream() {
        return valueList.stream();
    }

    @Override
    protected String getValueIndicator() {
        return "key_value";
    }

    @Override
    protected boolean isValueActivated(Map<String, String> value) {
        return true;
    }

    @Override
    protected boolean canViewValue(UUID uuid, Map<String, String> value) {
        return true;
    }

    @Override
    protected void configure(Map<String, Object> section) {
        valueList = Optional.ofNullable(MapUtils.getIfFound(section, "values", "value"))
                .filter(List.class::isInstance)
                .<List<?>>map(List.class::cast)
                .map(rawList -> {
                    List<Map<String, String>> list = new ArrayList<>();
                    for (Object raw : rawList) {
                        if (!(raw instanceof Map)) {
                            continue;
                        }
                        Map<?, ?> rawMap = (Map<?, ?>) raw;
                        Map<String, String> map = new HashMap<>();
                        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                            String key = Objects.toString(entry.getKey(), "");
                            Object v = entry.getValue();
                            String value;
                            if (v instanceof Collection) {
                                List<String> stringList = new ArrayList<>();
                                ((Collection<?>) v).forEach(o -> stringList.add(Objects.toString(o, "")));
                                value = String.join("\n", stringList);
                            } else {
                                value = v.toString();
                            }
                            map.put(key, value);
                        }
                        list.add(map);
                    }
                    return list;
                })
                .orElse(Collections.emptyList());
    }
}
