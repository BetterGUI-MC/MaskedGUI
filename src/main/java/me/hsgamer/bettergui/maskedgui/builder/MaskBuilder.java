package me.hsgamer.bettergui.maskedgui.builder;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.hscore.builder.MassBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MaskBuilder extends MassBuilder<MaskBuilder.Input, WrappedMask> {
    public static final MaskBuilder INSTANCE = new MaskBuilder();

    private MaskBuilder() {
        super();
    }

    public void register(Function<Input, WrappedMask> creator, String... type) {
        register(new Element<Input, WrappedMask>() {
            @Override
            public boolean canBuild(Input input) {
                Map<String, Object> keys = new CaseInsensitiveStringMap<>(input.options);
                String mask = Objects.toString(keys.get("mask"), "simple");
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

    public List<WrappedMask> getChildMasks(WrappedMask parentMask, Map<String, Object> section) {
        return section.entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .map(entry -> {
                    // noinspection unchecked
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();
                    String name = parentMask.getName() + "_child_" + entry.getKey();
                    return new Input(parentMask.getMenu(), name, value);
                })
                .flatMap(input -> build(input).map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());
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
