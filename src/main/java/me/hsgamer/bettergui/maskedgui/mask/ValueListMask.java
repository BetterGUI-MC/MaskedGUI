/*
   Copyright 2023-2023 Huynh Tien

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
package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.replacer.ValueReplacer;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.bettergui.maskedgui.util.RequirementUtil;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.requirement.type.ConditionRequirement;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.bukkit.scheduler.Task;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.hscore.minecraft.gui.GUIProperties;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.mask.impl.ButtonPaginatedMask;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ValueListMask<T> extends WrappedPaginatedMask<ButtonPaginatedMask> {
    private final ValueReplacer<T> valueReplacer;
    private final Map<T, ValueEntry<T>> valueEntryMap = new ConcurrentHashMap<>();
    private final Map<UUID, ValueListCache> playerListCacheMap = new ConcurrentHashMap<>();
    private final Function<Runnable, Task> scheduler;
    protected long valueUpdateTicks = 20L;
    protected long viewerUpdateMillis = 50L;
    private Map<String, Object> templateButton = Collections.emptyMap();
    private List<String> viewerConditionTemplate = Collections.emptyList();
    private Map<String, Object> viewerRequirementTemplate = Collections.emptyMap();
    private Task updateTask;

    protected ValueListMask(Function<Runnable, Task> scheduler, MaskBuilder.Input input) {
        super(input);
        this.scheduler = scheduler;
        this.valueReplacer = createValueReplacer();
    }

    protected ValueListMask(MaskBuilder.Input input) {
        super(input);
        this.scheduler = runnable -> Scheduler.current().async().runTaskTimer(runnable, 0L, valueUpdateTicks);
        this.valueReplacer = createValueReplacer();
    }

    protected abstract ValueReplacer<T> createValueReplacer();

    protected abstract Stream<T> getValueStream();

    protected abstract String getValueIndicator();

    protected String getValueAsString(T value) {
        return Objects.toString(value);
    }

    protected abstract boolean isValueActivated(T value);

    protected abstract boolean canViewValue(UUID uuid, T value);

    protected void configure(Map<String, Object> section) {
        // EMPTY
    }

    private Object replace(Object obj, T value) {
        if (obj instanceof String) {
            return valueReplacer.replace((String) obj, value);
        } else if (obj instanceof Collection) {
            List<Object> replaceList = new ArrayList<>();
            ((Collection<?>) obj).forEach(o -> replaceList.add(replace(o, value)));
            return replaceList;
        } else if (obj instanceof Map) {
            // noinspection unchecked, rawtypes
            ((Map) obj).replaceAll((k, v) -> replace(v, value));
        }
        return obj;
    }

    private Map<String, Object> replace(Map<String, Object> map, T value) {
        Map<String, Object> newMap = new LinkedHashMap<>();
        map.forEach((k, v) -> newMap.put(k, replace(v, value)));
        return newMap;
    }

    private List<String> replace(List<String> list, T value) {
        List<String> newList = new ArrayList<>();
        list.forEach(s -> newList.add(valueReplacer.replace(s, value)));
        return newList;
    }

    private boolean canView(UUID uuid, ValueEntry<T> valueEntry) {
        return valueEntry.activated.get() && canViewValue(uuid, valueEntry.value) && valueEntry.viewPredicate.test(uuid);
    }

    private ValueEntry<T> newValueEntry(T value) {
        Map<String, Object> replacedButtonSettings = replace(templateButton, value);
        Button button = ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), String.join("_", getName(), getValueIndicator(), getValueAsString(value), "button"), replacedButtonSettings))
                .map(Button.class::cast)
                .orElse(Button.EMPTY);
        button.init();

        Predicate<UUID> viewerPredicate = uuid -> true;

        if (!viewerConditionTemplate.isEmpty()) {
            List<String> replacedViewerConditions = replace(viewerConditionTemplate, value);
            ConditionRequirement viewerCondition = new ConditionRequirement(new RequirementBuilder.Input(getMenu(), "condition", String.join("_", getName(), getValueIndicator(), getValueAsString(value), "condition"), replacedViewerConditions));

            viewerPredicate = viewerPredicate.and(uuid -> viewerCondition.check(uuid).isSuccess);
        }

        if (!viewerRequirementTemplate.isEmpty()) {
            Map<String, Object> replacedViewerRequirements = replace(viewerRequirementTemplate, value);
            RequirementApplier viewerRequirementApplier = new RequirementApplier(getMenu(), String.join("_", getName(), getValueIndicator(), getValueAsString(value), "viewer"), replacedViewerRequirements);
            viewerPredicate = viewerPredicate.and(uuid -> RequirementUtil.check(uuid, viewerRequirementApplier));
        }

        return new ValueEntry<>(value, button, viewerPredicate);
    }

    private List<Button> getPlayerButtons(UUID uuid) {
        return playerListCacheMap.compute(uuid, (u, cache) -> {
            long now = System.currentTimeMillis();
            if (cache != null) {
                long remaining = now - cache.lastUpdate;
                if (remaining < viewerUpdateMillis) {
                    return cache;
                }
            }
            return new ValueListCache(
                    now,
                    getValueStream()
                            .map(valueEntryMap::get)
                            .filter(Objects::nonNull)
                            .filter(entry -> canView(uuid, entry))
                            .map(entry -> entry.button)
                            .collect(Collectors.toList())
            );
        }).buttonList;
    }

    @Override
    protected ButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        templateButton = Optional.ofNullable(MapUtils.getIfFound(section, "template", "button"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElse(Collections.emptyMap());
        viewerConditionTemplate = Optional.ofNullable(MapUtils.getIfFound(section, "viewer-condition"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(Collections.emptyList());
        viewerRequirementTemplate = Optional.ofNullable(MapUtils.getIfFound(section, "viewer-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .orElse(Collections.emptyMap());
        viewerUpdateMillis = Optional.ofNullable(MapUtils.getIfFound(section, "viewer-update-ticks", "viewer-update"))
                .map(String::valueOf)
                .flatMap(Validate::getNumber)
                .map(BigDecimal::longValue)
                .map(ticks -> Math.max(ticks, 1) * GUIProperties.getMillisPerTick())
                .map(millis -> Math.max(millis, 1L))
                .orElse(50L);
        valueUpdateTicks = Optional.ofNullable(MapUtils.getIfFound(section, "value-update-ticks", "value-update"))
                .map(String::valueOf)
                .map(Long::parseLong)
                .orElse(20L);
        this.configure(section);
        return new ButtonPaginatedMask(getName(), MultiSlotUtil.getMaskSlot(section, this)) {
            @Override
            public @NotNull List<@NotNull Button> getButtons(@NotNull UUID uuid) {
                return getPlayerButtons(uuid);
            }
        };
    }

    @Override
    public void init() {
        super.init();
        updateTask = scheduler.apply(this::updateValueList);
    }

    @Override
    public void stop() {
        super.stop();
        if (updateTask != null) {
            updateTask.cancel();
        }
        valueEntryMap.values().forEach(playerEntry -> playerEntry.button.stop());
        valueEntryMap.clear();
    }

    private void updateValueList() {
        getValueStream().forEach(value -> valueEntryMap.compute(value, (currentValue, currentEntry) -> {
            if (currentEntry == null) {
                currentEntry = newValueEntry(value);
            }
            currentEntry.activated.lazySet(isValueActivated(value));
            return currentEntry;
        }));
    }

    private static class ValueEntry<T> {
        final T value;
        final Button button;
        final Predicate<UUID> viewPredicate;
        final AtomicBoolean activated = new AtomicBoolean();

        private ValueEntry(T value, Button button, Predicate<UUID> viewPredicate) {
            this.value = value;
            this.button = button;
            this.viewPredicate = viewPredicate;
        }
    }

    private static class ValueListCache {
        final long lastUpdate;
        final List<Button> buttonList;

        private ValueListCache(long lastUpdate, List<Button> buttonList) {
            this.lastUpdate = lastUpdate;
            this.buttonList = buttonList;
        }
    }
}
