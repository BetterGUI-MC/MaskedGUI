package me.hsgamer.bettergui.maskedgui.replacer;

import me.hsgamer.hscore.common.StringReplacer;

public interface ValueReplacer<T> {
    static <T> ValueReplacer<T> of(StringReplacer replacer) {
        return (string, value) -> replacer.tryReplace(string, null);
    }

    static <T> ValueReplacer<T> identity() {
        return (string, value) -> string;
    }

    String replace(String string, T value);

    default ValueReplacer<T> then(ValueReplacer<T> next) {
        return (string, value) -> next.replace(replace(string, value), value);
    }
}
