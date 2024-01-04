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
package me.hsgamer.bettergui.maskedgui.replacer;

import java.util.function.Function;

public class SimpleValueReplacer<T> implements ValueReplacer<T> {
    private final String valueIndicator;
    private final Function<T, String> valueFunction;

    public SimpleValueReplacer(String valueIndicator, Function<T, String> valueFunction) {
        this.valueIndicator = valueIndicator;
        this.valueFunction = valueFunction;
    }

    @Override
    public String replace(String string, T value) {
        return string.replace(valueIndicator, valueFunction.apply(value));
    }
}
