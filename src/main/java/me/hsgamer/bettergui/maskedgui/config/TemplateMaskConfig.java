package me.hsgamer.bettergui.maskedgui.config;

import me.hsgamer.bettergui.maskedgui.MaskedGUI;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;

import java.io.File;
import java.util.*;

public class TemplateMaskConfig {
    private final File templateFolder;
    private final Map<String, Config> templateMap = new HashMap<>();

    public TemplateMaskConfig(MaskedGUI addon) {
        this.templateFolder = new File(addon.getDataFolder(), "template");
        if (!templateFolder.exists() && templateFolder.mkdirs()) {
            addon.getPlugin().getLogger().info("Created template mask folder");
        }
    }

    public void setup() {
        if (!templateFolder.isDirectory()) {
            return;
        }
        for (File subFile : Objects.requireNonNull(templateFolder.listFiles())) {
            if (!subFile.isFile() || !subFile.getName().toLowerCase(Locale.ROOT).endsWith(".yml")) {
                return;
            }
            Config config = new BukkitConfig(subFile);
            config.setup();
            for (String key : config.getKeys(false)) {
                templateMap.put(key, config);
            }
        }
    }

    public void clear() {
        this.templateMap.clear();
    }

    public void reload() {
        this.clear();
        this.setup();
    }

    public Optional<Map<String, Object>> get(String name) {
        return Optional.ofNullable(this.templateMap.get(name)).map(config -> config.getNormalizedValues(name, false));
    }

    public Collection<String> getAllTemplateButtonNames() {
        return this.templateMap.keySet();
    }
}
