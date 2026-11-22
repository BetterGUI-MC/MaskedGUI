package me.hsgamer.bettergui.maskedgui.mask.stream;

import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.mask.ValueListMask;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class SimpleValueStream implements ValueListMask.ValueStream<String> {
    private final List<String> valueList;

    public SimpleValueStream(MaskBuilder.Input input) {
        valueList = Optional.ofNullable(MapUtils.getIfFound(input.options, "values", "value"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(Collections.emptyList());
    }

    @Override
    public Object replace(String input, String value) {
        return input.equals("current_value") ? value : null;
    }

    @Override
    public Stream<String> getStream() {
        return valueList.stream();
    }
}
