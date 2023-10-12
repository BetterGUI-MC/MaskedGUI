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
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.minecraft.gui.mask.impl.ButtonPaginatedMask;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerListMask extends ValueListMask<UUID> {
    private final String variablePrefix;
    private ConditionRequirement playerCondition;
    private boolean viewSelf = true;
    private boolean viewOffline = true;

    public PlayerListMask(MaskBuilder.Input input) {
        super(input);
        this.variablePrefix = getName() + "_current_";
        input.menu.getVariableManager().register(variablePrefix, (original, uuid) -> {
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
    protected String getShortcutPatternPrefix() {
        return "current_player";
    }

    @Override
    protected Stream<UUID> getValueStream() {
        return (viewOffline ? Arrays.stream(Bukkit.getOfflinePlayers()) : Bukkit.getOnlinePlayers().stream())
                .map(OfflinePlayer::getUniqueId);
    }

    @Override
    protected String getShortcutReplacement(String argument, UUID value) {
        if (argument.isEmpty()) {
            return "{" + variablePrefix + getValueAsString(value) + ";player}";
        } else {
            boolean isPAPI = argument.startsWith("papi_");
            if (isPAPI) {
                argument = argument.substring(5);
            }
            return "{" + variablePrefix + getValueAsString(value) + ";" + argument + ";" + isPAPI + "}";
        }
    }

    @Override
    protected String getValueIndicator() {
        return "player";
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
    protected ButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        viewSelf = Optional.ofNullable(MapUtil.getIfFound(section, "view-self", "self"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(true);
        viewOffline = Optional.ofNullable(MapUtil.getIfFound(section, "view-offline", "offline"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        playerCondition = Optional.ofNullable(MapUtil.getIfFound(section, "player-condition"))
                .map(o -> new ConditionRequirement(new RequirementBuilder.Input(getMenu(), "condition", getName() + "_player_condition", o)))
                .orElse(null);
        valueUpdateTicks = Optional.ofNullable(MapUtil.getIfFound(section, "player-update-ticks", "player-update"))
                .map(String::valueOf)
                .map(Long::parseLong)
                .orElse(valueUpdateTicks);
        return super.createPaginatedMask(section);
    }
}
