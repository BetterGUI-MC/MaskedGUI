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
import me.hsgamer.bettergui.maskedgui.replacer.SimpleValueReplacer;
import me.hsgamer.bettergui.maskedgui.replacer.ValueReplacer;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class SimpleValueListMask extends ValueListMask<String> {
    private List<String> valueList = Collections.emptyList();

    public SimpleValueListMask(MaskBuilder.Input input) {
        super(input);
    }

    @Override
    protected ValueReplacer<String> createValueReplacer() {
        return new SimpleValueReplacer<>("{current_value}", Function.identity());
    }

    @Override
    protected Stream<String> getValueStream() {
        return valueList.stream();
    }

    @Override
    protected String getValueIndicator() {
        return "simple";
    }

    @Override
    protected boolean isValueActivated(String value) {
        return true;
    }

    @Override
    protected boolean canViewValue(UUID uuid, String value) {
        return true;
    }

    @Override
    protected void configure(Map<String, Object> section) {
        valueList = Optional.ofNullable(MapUtils.getIfFound(section, "values", "value"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(Collections.emptyList());
    }
}
