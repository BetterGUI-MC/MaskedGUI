package me.hsgamer.bettergui.maskedgui.mask.stream;

import me.hsgamer.bettergui.builder.RequirementBuilder;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.mask.ValueListMask;
import me.hsgamer.bettergui.requirement.type.ConditionRequirement;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.common.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerStream implements ValueListMask.ValueStream<UUID> {
    private static final String KEY = "current_player";
    private static final String PREFIX = KEY + "_";
    private static final String PAPI_PREFIX = "papi_";

    private final String variablePrefix;
    private final ConditionRequirement playerCondition;
    private final boolean viewSelf;
    private final boolean viewOffline;

    public PlayerStream(MaskBuilder.Input input) {
        this.variablePrefix = input.name + "_current_";
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
        viewSelf = Optional.ofNullable(MapUtils.getIfFound(section, "view-self", "self"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(true);
        viewOffline = Optional.ofNullable(MapUtils.getIfFound(section, "view-offline", "offline"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(false);
        playerCondition = Optional.ofNullable(MapUtils.getIfFound(section, "player-condition"))
                .map(o -> new ConditionRequirement(new RequirementBuilder.Input(input.menu, "condition", input.name + "_player_condition", o)))
                .orElse(null);
    }

    @Override
    public Object replace(String input, UUID value) {
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
    public Stream<UUID> getStream() {
        return (viewOffline ? Arrays.stream(Bukkit.getOfflinePlayers()) : Bukkit.getOnlinePlayers().stream())
                .map(OfflinePlayer::getUniqueId);
    }

    @Override
    public Stream<UUID> getStream(UUID uuid) {
        Stream<UUID> stream = getStream();
        return viewSelf ? stream : stream.filter(u -> !u.equals(uuid));
    }

    @Override
    public boolean isActivated(UUID value) {
        return playerCondition == null || playerCondition.check(value).isSuccess;
    }
}
