package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.bettergui.maskedgui.api.mask.BaseWrappedMask;
import me.hsgamer.bettergui.maskedgui.api.mask.WrappedMask;
import me.hsgamer.bettergui.maskedgui.api.signal.Signal;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringMap;

import java.util.*;

public class TemplateMask extends BaseWrappedMask<WrappedMask> {
    private final MaskedGUI addon;

    public TemplateMask(MaskedGUI addon, MaskBuilder.Input input) {
        super(input);
        this.addon = addon;
    }

    @Override
    protected WrappedMask createMask(Map<String, Object> section) {
        Map<String, Object> finalMap = new LinkedHashMap<>();

        Map<String, Object> keys = new CaseInsensitiveStringMap<>(section);
        Optional.ofNullable(keys.get("template"))
                .map(String::valueOf)
                .flatMap(s -> addon.getTemplateMaskConfig().get(s))
                .ifPresent(finalMap::putAll);
        Map<String, String> variableMap = new HashMap<>();
        // noinspection unchecked
        Optional.ofNullable(keys.get("variable"))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .ifPresent(map -> map.forEach((k, v) -> {
                    String variable = String.valueOf(k);
                    String value;
                    if (v instanceof List) {
                        List<String> list = new ArrayList<>();
                        ((List<?>) v).forEach(o -> list.add(String.valueOf(o)));
                        value = String.join("\n", list);
                    } else {
                        value = String.valueOf(v);
                    }
                    variableMap.put(variable, value);
                }));
        keys.entrySet()
                .stream()
                .filter(entry ->
                        !entry.getKey().equalsIgnoreCase("variable")
                                && !entry.getKey().equalsIgnoreCase("mask")
                                && !entry.getKey().equalsIgnoreCase("template")
                )
                .forEach(entry -> finalMap.put(entry.getKey(), entry.getValue()));
        if (!variableMap.isEmpty()) {
            finalMap.replaceAll((s, o) -> replaceVariables(o, variableMap));
        }

        return MaskBuilder.INSTANCE.build(new MaskBuilder.Input(getMenu(), getName(), finalMap)).orElse(null);
    }

    @Override
    public void refresh(UUID uuid) {
        if (mask != null) {
            mask.refresh(uuid);
        }
    }

    @Override
    public void handleSignal(UUID uuid, Signal signal) {
        if (mask != null) {
            mask.handleSignal(uuid, signal);
        }
    }

    private Object replaceVariables(Object obj, Map<String, String> variableMap) {
        if (obj instanceof String) {
            String string = (String) obj;
            for (Map.Entry<String, String> entry : variableMap.entrySet()) {
                string = string.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return string;
        } else if (obj instanceof Collection) {
            List<Object> replaceList = new ArrayList<>();
            ((Collection<?>) obj).forEach(o -> replaceList.add(replaceVariables(o, variableMap)));
            return replaceList;
        } else if (obj instanceof Map) {
            // noinspection unchecked, rawtypes
            ((Map) obj).replaceAll((k, v) -> replaceVariables(v, variableMap));
        }
        return obj;
    }
}
