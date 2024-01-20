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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleVariableValueReplacer<T> extends PatternValueReplacer<T> {
    protected abstract String getPrefix();

    protected abstract String replaceVariable(String argument, T value);

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("\\{" + Pattern.quote(getPrefix()) + "(_([^{}]+))?}");
    }

    @Override
    protected String replace(Matcher matcher, T value) {
        String variable = matcher.group(2);
        return replaceVariable(variable == null ? "" : variable, value);
    }
}
