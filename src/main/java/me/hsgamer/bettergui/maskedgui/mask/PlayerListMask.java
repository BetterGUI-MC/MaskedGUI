package me.hsgamer.bettergui.maskedgui.mask;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.bettergui.maskedgui.builder.MaskBuilder;
import me.hsgamer.bettergui.maskedgui.util.MultiSlotUtil;
import me.hsgamer.bettergui.util.MapUtil;
import me.hsgamer.bettergui.util.StringReplacerApplier;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.mask.impl.ButtonPaginatedMask;
import me.hsgamer.hscore.variable.VariableManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlayerListMask extends WrappedPaginatedMask<ButtonPaginatedMask> implements Runnable {
    private static final Pattern pattern = Pattern.compile("\\{current_player(_(.+))?}");

    static {
        VariableManager.register("current_", (original, uuid) -> {
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
            return StringReplacerApplier.replace(finalVariable, targetId, true);
        });
    }

    private final Map<UUID, Button> buttonMap = new ConcurrentHashMap<>();
    private final MaskedGUI addon;
    private Map<String, Object> templateButton = Collections.emptyMap();
    private BukkitTask updateTask;
    private boolean viewSelf = true;

    public PlayerListMask(MaskedGUI addon, MaskBuilder.Input input) {
        super(input);
        this.addon = addon;
    }

    private static Object replaceShortcut(Object obj, UUID targetId) {
        if (obj instanceof String) {
            String string = (String) obj;
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                String variable = matcher.group(2);
                String replacement;
                if (variable == null) {
                    replacement = "{current_" + targetId.toString() + ";player}";
                } else {
                    boolean isPAPI = variable.startsWith("papi_");
                    if (isPAPI) {
                        variable = variable.substring(5);
                    }
                    replacement = "{current_" + targetId.toString() + ";" + variable + ";" + isPAPI + "}";
                }
                string = string.replace(matcher.group(), replacement);
            }
            return string;
        } else if (obj instanceof Collection) {
            List<Object> replaceList = new ArrayList<>();
            ((Collection<?>) obj).forEach(o -> replaceList.add(replaceShortcut(o, targetId)));
            return replaceList;
        } else if (obj instanceof Map) {
            // noinspection unchecked, rawtypes
            ((Map) obj).replaceAll((k, v) -> replaceShortcut(v, targetId));
        }
        return obj;
    }

    private static Map<String, Object> replace(Map<String, Object> map, UUID targetId) {
        Map<String, Object> newMap = new LinkedHashMap<>();
        map.forEach((k, v) -> newMap.put(k, replaceShortcut(v, targetId)));
        return newMap;
    }

    private boolean canView(UUID uuid, UUID targetId) {
        if (viewSelf && !uuid.equals(targetId)) {
            return false;
        }

        // TODO: Add requirements to check between players
        return false;
    }

    private Button newButton(UUID uuid) {
        Map<String, Object> replaced = replace(templateButton, uuid);
        Button button = ButtonBuilder.INSTANCE.build(new ButtonBuilder.Input(getMenu(), getName() + "_" + uuid.toString(), replaced))
                .map(Button.class::cast)
                .orElse(Button.EMPTY);
        button.init();
        return button;
    }

    private List<Button> getPlayerButtons(UUID uuid) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getUniqueId)
                .filter(targetId -> canView(uuid, targetId))
                .map(buttonMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    protected ButtonPaginatedMask createPaginatedMask(Map<String, Object> section) {
        templateButton = Optional.ofNullable(MapUtil.getIfFound(section, "template", "button"))
                .flatMap(MapUtil::castOptionalStringObjectMap)
                .orElse(Collections.emptyMap());
        viewSelf = Optional.ofNullable(MapUtil.getIfFound(section, "view-self", "self"))
                .map(String::valueOf)
                .map(Boolean::parseBoolean)
                .orElse(true);
        return new ButtonPaginatedMask(getName(), MultiSlotUtil.getSlots(section)) {
            @Override
            public @NotNull List<@NotNull Button> getButtons(@NotNull UUID uuid) {
                return getPlayerButtons(uuid);
            }
        };
    }

    @Override
    public void init() {
        super.init();
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(addon.getPlugin(), this, 0L, 20L);
    }

    @Override
    public void stop() {
        super.stop();
        if (updateTask != null) {
            updateTask.cancel();
        }
        buttonMap.values().forEach(Button::stop);
        buttonMap.clear();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            buttonMap.computeIfAbsent(player.getUniqueId(), this::newButton);
        }
    }
}
