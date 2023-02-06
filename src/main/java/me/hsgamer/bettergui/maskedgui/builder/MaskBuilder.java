package me.hsgamer.bettergui.maskedgui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringLinkedMap;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MaskBuilder extends MassBuilder<MaskBuilder.Input, WrappedMask> {
    public static final MaskBuilder INSTANCE = new MaskBuilder();
    private String defaultMaskType = "";

    private MaskBuilder() {
        super();
    }

    public void setDefaultMaskType(String defaultMaskType) {
        this.defaultMaskType = defaultMaskType;
    }

    public void register(Function<Input, WrappedMask> creator, String... type) {
        register(new Element<Input, WrappedMask>() {
            @Override
            public boolean canBuild(Input input) {
                Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
                String mask = Objects.toString(keys.get("mask"), defaultMaskType);
                for (String s : type) {
                    if (mask.equalsIgnoreCase(s)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public WrappedMask build(Input input) {
                return creator.apply(input);
            }
        });
    }

    public Map<String, WrappedMask> getChildMasksAsMap(WrappedMask parentMask, Map<String, Object> section) {
        return section.entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .map(entry -> {
                    // noinspection unchecked
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                    String name = parentMask.getName() + "_child_" + entry.getKey();
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), new Input(parentMask.getMenu(), name, value));
                })
                .flatMap(entry -> {
                    WrappedMask mask = build(entry.getValue()).orElse(null);
                    if (mask == null) {
                        return Stream.empty();
                    }
                    return Stream.of(new AbstractMap.SimpleEntry<>(entry.getKey(), mask));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, CaseInsensitiveStringLinkedMap::new));
    }

    public static class Input {
        public final Menu menu;
        public final String name;
        public final Map<String, Object> options;

        public Input(Menu menu, String name, Map<String, Object> options) {
            this.menu = menu;
            this.name = name;
            this.options = options;
        }
    }
}
