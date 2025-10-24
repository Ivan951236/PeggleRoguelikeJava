package com.peggle;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

public class ThemeManager {
    
    public enum Theme {
        LIGHT, DARK
    }
    
    public interface ThemeChangeListener {
        void onThemeChanged(Theme theme);
    }
    
    public static class Palette {
        public Color primary;
        public Color onPrimary;
        public Color background;
        public Color onBackground;
        public Color surface;
        public Color onSurface;
        public Color outline;

        public Palette(Color primary, Color onPrimary, Color background, Color onBackground, Color surface, Color onSurface, Color outline) {
            this.primary = primary;
            this.onPrimary = onPrimary;
            this.background = background;
            this.onBackground = onBackground;
            this.surface = surface;
            this.onSurface = onSurface;
            this.outline = outline;
        }
    }
    
    private static ThemeManager instance;
    private Theme currentTheme;
    private String currentVariant;
    private final Map<String, Palette> variantsLight = new HashMap<>();
    private final Map<String, Palette> variantsDark = new HashMap<>();
    private final List<ThemeChangeListener> listeners;

    private final File customThemesDir;

    private ThemeManager() {
        currentTheme = Theme.LIGHT;
        currentVariant = "blue";
        listeners = new ArrayList<>();
        customThemesDir = new File(getAppDir(), "customThemes");
        if (!customThemesDir.exists()) customThemesDir.mkdirs();
        initDefaultPalettes();
        loadCustomThemes();
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private static File getAppDir() {
        return ConfigManager.getInstance().getConfigFile().getParentFile();
    }

    private void initDefaultPalettes() {
        // Material Design 3-like palettes (approximate)
        addVariant("red", 0xB3261E);
        addVariant("orange", 0xC25E00);
        addVariant("yellow", 0x9A8700);
        addVariant("green", 0x0B6B3A);
        addVariant("blue", 0x1B6EF3);
        addVariant("cyan", 0x007F91);
        addVariant("purple", 0x6750A4);
        addVariant("pink", 0x9A4050);
    }

    private void addVariant(String name, int primaryHex) {
        Color primary = new Color(primaryHex);
        // Light palette
        variantsLight.put(name, new Palette(
                primary,
                Color.WHITE,
                new Color(0xFFFBFE), // background
                Color.BLACK,         // onBackground
                Color.WHITE,         // surface
                Color.BLACK,         // onSurface
                new Color(0x79747E)  // outline
        ));
        // Dark palette
        variantsDark.put(name, new Palette(
                lighten(primary, -0.1f),
                Color.BLACK,
                new Color(0x1C1B1F),
                Color.WHITE,
                new Color(0x1C1B1F),
                Color.WHITE,
                new Color(0x938F99)
        ));
    }

    private static Color lighten(Color c, float delta) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float b = Math.min(1f, Math.max(0f, hsb[2] + delta));
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], b);
        return new Color(rgb & 0xFFFFFF);
    }

    @SuppressWarnings("unchecked")
    private void loadCustomThemes() {
        File[] files = customThemesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml") || name.toLowerCase().endsWith(".yaml"));
        if (files == null) return;
        for (File f : files) {
            try (FileInputStream fis = new FileInputStream(f)) {
                Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
                Object data = yaml.load(fis);
                if (!(data instanceof Map)) continue;
                Map<String, Object> m = (Map<String, Object>) data;
                String name = String.valueOf(m.getOrDefault("name", f.getName()));
                String mode = String.valueOf(m.getOrDefault("mode", "light")).toLowerCase();
                int primary = parseHex(String.valueOf(m.getOrDefault("primary", "#6750A4")));
                int onPrimary = parseHex(String.valueOf(m.getOrDefault("onPrimary", mode.equals("dark")?"#000000":"#FFFFFF")));
                int background = parseHex(String.valueOf(m.getOrDefault("background", mode.equals("dark")?"#1C1B1F":"#FFFBFE")));
                int onBackground = parseHex(String.valueOf(m.getOrDefault("onBackground", mode.equals("dark")?"#FFFFFF":"#000000")));
                int surface = parseHex(String.valueOf(m.getOrDefault("surface", background)));
                int onSurface = parseHex(String.valueOf(m.getOrDefault("onSurface", onBackground)));
                int outline = parseHex(String.valueOf(m.getOrDefault("outline", mode.equals("dark")?"#938F99":"#79747E")));
                Palette p = new Palette(new Color(primary), new Color(onPrimary), new Color(background), new Color(onBackground), new Color(surface), new Color(onSurface), new Color(outline));
                if (mode.equals("dark")) variantsDark.put(name, p); else variantsLight.put(name, p);
            } catch (Exception ignored) {}
        }
    }

    private int parseHex(String s) {
        s = s.trim();
        if (s.startsWith("#")) s = s.substring(1);
        return (int)Long.parseLong(s, 16);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setTheme(Theme theme) {
        if (currentTheme != theme) {
            currentTheme = theme;
            ConfigManager.getInstance().set("themeMode", theme.name());
            ConfigManager.getInstance().save();
            notifyListeners();
        }
    }

    public String getCurrentVariant() { return currentVariant; }

    public void setVariant(String variant) {
        if (!Objects.equals(this.currentVariant, variant) && getPaletteMap().containsKey(variant)) {
            this.currentVariant = variant;
            ConfigManager.getInstance().set("themeVariant", variant);
            ConfigManager.getInstance().save();
            notifyListeners();
        }
    }

    public Set<String> availableVariants() {
        Set<String> s = new TreeSet<>();
        s.addAll(variantsLight.keySet());
        s.addAll(variantsDark.keySet());
        return s;
    }

    private Map<String, Palette> getPaletteMap() {
        return currentTheme == Theme.LIGHT ? variantsLight : variantsDark;
    }

    private Palette palette() {
        Map<String, Palette> map = getPaletteMap();
        return map.getOrDefault(currentVariant, map.values().stream().findFirst().orElse(new Palette(Color.GRAY, Color.WHITE, Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK, Color.GRAY)));
    }

    public void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(currentTheme);
        }
    }

    public Color getTextColor() { // on surface
        return palette().onSurface;
    }
    
    public Color getBackgroundColor() {
        return palette().background;
    }
    
    public Color getSurfaceColor() {
        return palette().surface;
    }
    
    public Color getOnPrimaryColor() {
        return palette().onPrimary;
    }
    
    public Color getForegroundColor() { // primary container-ish for labels
        return palette().primary;
    }
    
    public Color getButtonColor() {
        return palette().primary;
    }
    
    public Color getOutlineColor() {
        return palette().outline;
    }
}
