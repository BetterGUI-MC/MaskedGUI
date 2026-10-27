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

import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.requirement.type.ConditionRequirement;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.bettergui.util.TickUtil;
import me.hsgamer.hscore.common.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerListMask extends ValueListMask<UUID> {
    private static final String KEY = "current_player";
    private static final String PREFIX = KEY + "_";
    private static final String PAPI_PREFIX = "papi_";

    private final String variablePrefix;
    private ConditionRequirement playerCondition;
    private boolean viewSelf = true;
    private boolean viewOffline = true;

    public PlayerListMask(MaskBuilder.Input input) {
        super(input);
        this.variablePrefix = getName() + "_current_";
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
            return StringReplacerApplier.replace(finalVariable, targetId, this);
        });
    }

    @Override
    protected String replace(String input, UUID value) {
        if (input.equals(KEY)) {
            return "{" + variablePrefix + value + ";player}";
        }
        if (input.startsWith(PREFIX)) {
            String variable = input.substring(PREFIX.length());
            boolean isPAPI = input.startsWith(PAPI_PREFIX);
            if (isPAPI) {
                variable = variable.substring(PAPI_PREFIX.length());
            }
            return "{" + variablePrefix + value + ";" + variable + ";" + isPAPI + "}";
        }
        return null;
    }

    @Override
    protected Stream<UUID> getValueStream() {
        return (viewOffline ? Arrays.stream(Bukkit.getOfflinePlayers()) : Bukkit.getOnlinePlayers().stream())
                .map(OfflinePlayer::getUniqueId);
    }

    @Override
    protected boolean isValueActivated(UUID value) {
        return playerCondition == null || playerCondition.check(value).isSuccess;
    }

    @Override
    protected boolean canViewValue(UUID uuid, UUID value) {
        return viewSelf || !uuid.equals(value);
    }

    @Override
    protected void configure(Map<String, Object> section) {
        viewSelf = Optional.ofNullable(MapUtils.getIfFound(section, "view-self", "self"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(true);
        viewOffline = Optional.ofNullable(MapUtils.getIfFound(section, "view-offline", "offline"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        playerCondition = Optional.ofNullable(MapUtils.getIfFound(section, "player-condition"))
                .map(o -> new ConditionRequirement(new RequirementBuilder.Input(getMenu(), "condition", getName() + "_player_condition", o)))
                .orElse(null);
        valueUpdateMillis = Optional.ofNullable(MapUtils.getIfFound(section, "player-update-ticks", "player-update"))
                .map(String::valueOf)
                .flatMap(TickUtil::toMillis)
                .filter(n -> n > 0)
                .orElse(valueUpdateMillis);
    }
}
