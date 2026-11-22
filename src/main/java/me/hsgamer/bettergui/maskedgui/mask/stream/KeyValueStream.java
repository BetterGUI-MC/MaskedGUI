package me.hsgamer.bettergui.maskedgui.mask.stream;

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.mask.ValueListMask;
import me.hsgamer.hscore.common.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyValueStream implements ValueListMask.ValueStream<Map<String, Object>> {
    private static final String PREFIX = "key_";
    private final List<Map<String, Object>> valueList;

    public KeyValueStream(MaskBuilder.Input input) {
        valueList = Optional.ofNullable(MapUtils.getIfFound(input.options, "values", "value"))
                .filter(List.class::isInstance)
                .<List<?>>map(List.class::cast)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(o -> MapUtils.castOptionalStringObjectMap(o, false).map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());
    }

    @Override
    public Object replace(String input, Map<String, Object> value) {
        if (!input.startsWith(PREFIX)) {
            return null;
        }
        String key = input.substring(PREFIX.length());
        return value.getOrDefault(key, "");
    }

    @Override
    public Stream<Map<String, Object>> getStream() {
        return valueList.stream();
    }
}
