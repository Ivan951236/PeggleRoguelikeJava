package com.peggle;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "config.yml";

    private static ConfigManager instance;

    private final File configFile;
    private Map<String, Object> config;

    private ConfigManager() {
        this.configFile = new File(getAppDir(), CONFIG_FILE_NAME);
        this.config = new HashMap<>();
        load();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public File getConfigFile() {
        return configFile;
    }

    @SuppressWarnings("unchecked")
    public synchronized void load() {
        // Defaults
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("mode", "singleplayer");
        defaults.put("player1Name", "Player 1");
        defaults.put("player2Name", "Player 2");
        defaults.put("theme", ThemeManager.Theme.LIGHT.name());
        Map<String, Object> levels = new HashMap<>();
        levels.put("roguelikeWins", 0);
        levels.put("levelWins", 0);
        levels.put("clutches", 0);
        levels.put("totalMisses", 0);
        levels.put("lostRounds", 0);
        levels.put("lostOnBossLevel", 0);
        levels.put("feverHundredKs", 0);
        defaults.put("levels", levels);

        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
                Object data = yaml.load(fis);
                if (data instanceof Map) {
                    config = (Map<String, Object>) data;
                }
            } catch (Exception ignored) {}
        }

        if (config == null) config = new HashMap<>();
        // Merge defaults
        mergeDefaults(config, defaults);
    }

    @SuppressWarnings("unchecked")
    private void mergeDefaults(Map<String, Object> target, Map<String, Object> defaults) {
        for (Map.Entry<String, Object> e : defaults.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            if (!target.containsKey(k)) {
                target.put(k, v);
            } else if (v instanceof Map && target.get(k) instanceof Map) {
                mergeDefaults((Map<String, Object>) target.get(k), (Map<String, Object>) v);
            }
        }
    }

    public synchronized void save() {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            try (FileWriter writer = new FileWriter(configFile)) {
                yaml.dump(config, writer);
            }
        } catch (Exception ignored) {}
    }

    public synchronized Map<String, Object> getConfig() {
        return config;
    }

    public synchronized void set(String key, Object value) {
        config.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public synchronized void setLevelField(String key, Object value) {
        Map<String, Object> levels = (Map<String, Object>) config.get("levels");
        if (levels == null) {
            levels = new HashMap<>();
            config.put("levels", levels);
        }
        levels.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public synchronized Object getLevelField(String key) {
        Map<String, Object> levels = (Map<String, Object>) config.get("levels");
        return levels != null ? levels.get(key) : null;
    }

    private static File getAppDir() {
        try {
            CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File loc = new File(codeSource.getLocation().toURI());
                if (loc.isFile()) {
                    return loc.getParentFile();
                } else {
                    return loc; // likely running from classes dir
                }
            }
        } catch (URISyntaxException ignored) {}
        return new File(System.getProperty("user.dir"));
    }
}
