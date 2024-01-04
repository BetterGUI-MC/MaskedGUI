package me.hsgamer.bettergui.maskedgui.replacer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleVariableValueReplacer<T> extends PatternValueReplacer<T> {
    protected abstract String getPrefix();

    protected abstract String replaceVariable(String argument, T value);

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("\\{" + Pattern.quote(getPrefix() + "(_([^{}]+))?}"));
    }

    @Override
    protected String replace(Matcher matcher, T value) {
        String variable = matcher.group(2);
        return replaceVariable(variable == null ? "" : variable, value);
    }
}
