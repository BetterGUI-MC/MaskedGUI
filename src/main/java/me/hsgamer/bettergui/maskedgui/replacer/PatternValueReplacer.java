package me.hsgamer.bettergui.maskedgui.replacer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PatternValueReplacer<T> implements ValueReplacer<T> {
    private final Pattern pattern;

    protected PatternValueReplacer() {
        this.pattern = createPattern();
    }

    protected abstract Pattern createPattern();

    protected abstract String replace(Matcher matcher, T value);

    @Override
    public String replace(String string, T value) {
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            string = string.replace(matcher.group(), replace(matcher, value));
        }
        return string;
    }
}
