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
