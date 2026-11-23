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

import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.mask.ButtonPaginatedMask;
import io.github.projectunified.maptemplate.MapTemplate;
import io.github.projectunified.maptemplate.VariableStringReplacer;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.valuevenue.bukkit.BukkitPlayerSource;
import io.github.projectunified.valuevenue.common.KeyValueSource;
import io.github.projectunified.valuevenue.common.SimpleValueSource;
import io.github.projectunified.valuevenue.core.CachedValueGenerator;
import io.github.projectunified.valuevenue.core.ValueSource;
import io.github.projectunified.valuevenue.core.ValueVenue;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.ButtonUtil;
import me.hsgamer.bettergui.maskedgui.util.MaskSlotUtil;
import me.hsgamer.bettergui.maskedgui.util.RequirementUtil;
import me.hsgamer.bettergui.requirement.RequirementApplier;
import me.hsgamer.bettergui.requirement.type.ConditionRequirement;
import me.hsgamer.bettergui.util.SchedulerUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.MapUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueListMask extends WrappedPaginatedMask<ButtonPaginatedMask> {
    private final ValueSource<UUID, ?> valueSource;
    private final Map<UUID, Long> lastUpdateMap = new ConcurrentHashMap<>();

    protected long valueUpdateMillis = 1000L;
    protected long viewerUpdateMillis = 50L;
    private MapTemplate templateButton;
    private MapTemplate viewerConditionTemplate;
    private MapTemplate viewerRequirementTemplate;

    private CachedValueGenerator<UUID, ?, ButtonEntry> valueGenerator;
    private ValueVenue<UUID, Button> valueVenue;
    private Task updateTask;

    public ValueListMask(MaskBuilder.Input input, ValueSources valueSource) {
        super(input);
        this.valueSource = valueSource.valueSource(input);
    }

    public static SimpleValueSource simpleValueSource(MaskBuilder.Input input) {
        List<String> valueList = Optional.ofNullable(MapUtils.getIfFound(input.options, "values", "value"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(Collections.emptyList());
        return new SimpleValueSource(valueList);
    }

    public static KeyValueSource keyValueSource(MaskBuilder.Input input) {
        List<Map<String, Object>> valueList = Optional.ofNullable(MapUtils.getIfFound(input.options, "values", "value"))
                .filter(List.class::isInstance)
                .<List<?>>map(List.class::cast)
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(o -> MapUtils.castOptionalStringObjectMap(o, false).map(Stream::of).orElseGet(Stream::empty))
                .collect(Collectors.toList());
        return new KeyValueSource(valueList);
    }

    public static BukkitPlayerSource playerSource(MaskBuilder.Input input) {
        String variablePrefix = input.name + "_current_";
        input.menu.getVariableManager().register(variablePrefix, original -> {
            String[] split = original.split(";", 3);
            if (split.length < 2) {
                return null;
            }
            UUID targetId;
            try {
                targetId = UUID.fromString(split[0]);
            } catch (IllegalArgumentException e) {
                return null;
            }
            String variable = split[1];
            boolean isPAPI = split.length == 3 && Boolean.parseBoolean(split[2]);
            String finalVariable;
            if (isPAPI) {
                finalVariable = "%" + variable + "%";
            } else {
                finalVariable = "{" + variable + "}";
            }
            return StringReplacerApplier.replace(finalVariable, targetId, input.menu);
        });

        Map<String, Object> section = input.options;
        boolean viewSelf = Optional.ofNullable(MapUtils.getIfFound(section, "view-self", "self"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(true);
        boolean viewOffline = Optional.ofNullable(MapUtils.getIfFound(section, "view-offline", "offline"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        ConditionRequirement playerCondition = Optional.ofNullable(MapUtils.getIfFound(section, "player-condition"))
                .map(o -> new ConditionRequirement(new RequirementBuilder.Input(input.menu, "condition", input.name + "_player_condition", o)))
                .orElse(null);

        return new BukkitPlayerSource(viewSelf, viewOffline, player -> playerCondition == null || playerCondition.check(player.getUniqueId()).isSuccess) {
            private static final String PAPI_PREFIX = "papi_";

            @Override
            protected Object replaceVariable(String variable, OfflinePlayer value) {
                boolean isPAPI = variable.startsWith(PAPI_PREFIX);
                if (isPAPI) {
                    variable = variable.substring(PAPI_PREFIX.length());
                }
                return "{" + variablePrefix + value + ";" + variable + ";" + isPAPI + "}";
            }
        };
    }

    @Override
    protected ButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        templateButton = Optional.ofNullable(MapUtils.getIfFound(section, "template", "button"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(MapTemplate::mark)
                .orElse(null);
        viewerConditionTemplate = Optional.ofNullable(MapUtils.getIfFound(section, "viewer-condition"))
                .map(CollectionUtils::createStringListFromObject)
                .map(MapTemplate::mark)
                .orElse(null);
        viewerRequirementTemplate = Optional.ofNullable(MapUtils.getIfFound(section, "viewer-requirement"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(MapTemplate::mark)
                .orElse(null);
        viewerUpdateMillis = Optional.ofNullable(MapUtils.getIfFound(section, "viewer-update-ticks", "viewer-update"))
                .map(String::valueOf)
                .flatMap(TickUtil::toMillis)
                .filter(n -> n > 0)
                .orElse(50L);
        valueUpdateMillis = Optional.ofNullable(MapUtils.getIfFound(section, "value-update-ticks", "value-update", "player-update-ticks", "player-update"))
                .map(String::valueOf)
                .flatMap(TickUtil::toMillis)
                .filter(n -> n > 0)
                .orElse(50L);
        return new ButtonPaginatedMask(MaskSlotUtil.of(section, this)) {
            @Override
            public @NotNull List<Button> getButtons(@NotNull UUID uuid) {
                if (valueVenue == null) return Collections.emptyList();
                long lastUpdate = lastUpdateMap.getOrDefault(uuid, 0L);
                long now = System.currentTimeMillis();
                if (now - lastUpdate > viewerUpdateMillis) {
                    lastUpdateMap.put(uuid, now);
                    valueVenue.update(uuid);
                }
                return valueVenue.get(uuid);
            }
        };
    }

    @Override
    public void init() {
        super.init();
        this.valueGenerator = CachedValueGenerator.create(
                valueSource,
                (sourceValue, variableReplacer) -> {
                    VariableStringReplacer replacer = VariableStringReplacer.builder()
                            .setVariableFunction(variableReplacer)
                            .build();
                    Map<String, Object> replacedButtonSettings = templateButton == null
                            ? Collections.emptyMap()
                            : MapUtils.castOptionalStringObjectMap(templateButton.apply(replacer)).orElse(Collections.emptyMap());
                    Button button = ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), String.join("_", getName(), Objects.toString(sourceValue), "button"), replacedButtonSettings))
                            .<Button>map(ButtonUtil.CraftUXButton::new)
                            .orElseGet(() -> (uuid, item) -> false);
                    Element.handleIfElement(button, Element::init);

                    Predicate<UUID> viewerPredicate = uuid -> true;

                    if (viewerConditionTemplate != null) {
                        List<String> replacedViewerConditions = CollectionUtils.createStringListFromObject(viewerConditionTemplate.apply(replacer));
                        ConditionRequirement viewerCondition = new ConditionRequirement(new RequirementBuilder.Input(getMenu(), "condition", String.join("_", getName(), Objects.toString(sourceValue), "condition"), replacedViewerConditions));
                        viewerPredicate = viewerPredicate.and(uuid -> viewerCondition.check(uuid).isSuccess);
                    }

                    if (viewerRequirementTemplate != null) {
                        Map<String, Object> replacedViewerRequirements = MapUtils.castOptionalStringObjectMap(viewerRequirementTemplate).orElse(Collections.emptyMap());
                        RequirementApplier viewerRequirementApplier = new RequirementApplier(getMenu(), String.join("_", getName(), Objects.toString(sourceValue), "viewer"), replacedViewerRequirements);
                        viewerPredicate = viewerPredicate.and(uuid -> RequirementUtil.check(uuid, viewerRequirementApplier));
                    }

                    return new ButtonEntry(sourceValue, button, viewerPredicate);
                },
                (value, key) -> value.viewPredicate.test(key)
        );
        updateTask = SchedulerUtil.async().runTimer(valueGenerator::update, 0, valueUpdateMillis, TimeUnit.MILLISECONDS);
        this.valueVenue = new ValueVenue<>(valueGenerator.andThen(entryList -> entryList.stream().map(entry -> entry.button).collect(Collectors.toList())));
    }

    @Override
    public void stop() {
        super.stop();
        if (updateTask != null) {
            updateTask.cancel();
        }
        if (valueGenerator != null) {
            valueGenerator.getCachedValues().values().forEach(entry -> Element.handleIfElement(entry.button, Element::stop));
            valueGenerator = null;
        }
        valueVenue = null;
    }

    @Override
    protected void refresh(ButtonPaginatedMask mask, UUID uuid) {
        ButtonUtil.refreshCraftUXButtons(uuid, mask.getButtons(uuid));
    }

    public enum ValueSources {
        SIMPLE(input -> {
            List<String> valueList = Optional.ofNullable(MapUtils.getIfFound(input.options, "values", "value"))
                    .map(CollectionUtils::createStringListFromObject)
                    .orElse(Collections.emptyList());
            return new SimpleValueSource(valueList);
        }),
        KEY_VALUE(input -> {
            List<Map<String, Object>> valueList = Optional.ofNullable(MapUtils.getIfFound(input.options, "values", "value"))
                    .filter(List.class::isInstance)
                    .<List<?>>map(List.class::cast)
                    .orElse(Collections.emptyList())
                    .stream()
                    .flatMap(o -> MapUtils.castOptionalStringObjectMap(o, false).map(Stream::of).orElseGet(Stream::empty))
                    .collect(Collectors.toList());
            return new KeyValueSource(valueList);
        }),
        PLAYER(input -> {
            String variablePrefix = input.name + "_current_";
            input.menu.getVariableManager().register(variablePrefix, original -> {
                String[] split = original.split(";", 3);
                if (split.length < 2) {
                    return null;
                }
                UUID targetId;
                try {
                    targetId = UUID.fromString(split[0]);
                } catch (IllegalArgumentException e) {
                    return null;
                }
                String variable = split[1];
                boolean isPAPI = split.length == 3 && Boolean.parseBoolean(split[2]);
                String finalVariable;
                if (isPAPI) {
                    finalVariable = "%" + variable + "%";
                } else {
                    finalVariable = "{" + variable + "}";
                }
                return StringReplacerApplier.replace(finalVariable, targetId, input.menu);
            });

            Map<String, Object> section = input.options;
            boolean viewSelf = Optional.ofNullable(MapUtils.getIfFound(section, "view-self", "self"))
                    .map(String::valueOf)
                    .map(Boolean::parseBoolean)
                    .orElse(true);
            boolean viewOffline = Optional.ofNullable(MapUtils.getIfFound(section, "view-offline", "offline"))
                    .map(String::valueOf)
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            ConditionRequirement playerCondition = Optional.ofNullable(MapUtils.getIfFound(section, "player-condition"))
                    .map(o -> new ConditionRequirement(new RequirementBuilder.Input(input.menu, "condition", input.name + "_player_condition", o)))
                    .orElse(null);

            return new BukkitPlayerSource(viewSelf, viewOffline, player -> playerCondition == null || playerCondition.check(player.getUniqueId()).isSuccess) {
                private static final String PAPI_PREFIX = "papi_";

                @Override
                protected Object replaceVariable(String variable, OfflinePlayer value) {
                    boolean isPAPI = variable.startsWith(PAPI_PREFIX);
                    if (isPAPI) {
                        variable = variable.substring(PAPI_PREFIX.length());
                    }
                    return "{" + variablePrefix + value + ";" + variable + ";" + isPAPI + "}";
                }
            };
        });
        private final Function<MaskBuilder.Input, ValueSource<UUID, ?>> valueSourceFunction;

        ValueSources(Function<MaskBuilder.Input, ValueSource<UUID, ?>> valueSourceFunction) {
            this.valueSourceFunction = valueSourceFunction;
        }

        ValueSource<UUID, ?> valueSource(MaskBuilder.Input input) {
            return valueSourceFunction.apply(input);
        }
    }

    private static class ButtonEntry {
        final Object value;
        final Button button;
        final Predicate<UUID> viewPredicate;

        private ButtonEntry(Object value, Button button, Predicate<UUID> viewPredicate) {
            this.value = value;
            this.button = button;
            this.viewPredicate = viewPredicate;
        }
    }
}
