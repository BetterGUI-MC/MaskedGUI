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
import me.hsgamer.hscore.common.MapUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyValueListMask extends ValueListMask<Map<String, Object>> {
    private static final String PREFIX = "key_";
    private List<Map<String, Object>> valueList = Collections.emptyList();

    public KeyValueListMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected Object replace(String input, Map<String, Object> value) {
        if (!input.startsWith(PREFIX)) {
            return null;
        }
        String key = input.substring(PREFIX.length());
        return value.getOrDefault(key, "");
    }

    @Override
    protected Stream<Map<String, Object>> getValueStream() {
        return valueList.stream();
    }

    @Override
    protected void configure(Map<String, Object> section) {
        valueList = Optional.ofNullable(MapUtils.getIfFound(section, "values", "value"))
                .filter(List.class::isInstance)
                .<List<?>>map(List.class::cast)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(o -> MapUtils.castOptionalStringObjectMap(o, false).map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());
    }
}
