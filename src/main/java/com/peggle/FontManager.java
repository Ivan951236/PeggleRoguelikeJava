package com.peggle;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.InputStream;
import java.util.Locale;

public class FontManager {
    private static FontUIResource defaultFont;

    public static void installUbuntuNerdAsDefault(int size) {
        try {
            Font base = loadUbuntuNerdFromResources();
            if (base == null) base = findUbuntuNerdInstalled();
            if (base != null) {
                defaultFont = new FontUIResource(base.deriveFont((float) size));
                applyDefaultFont();
            }
        } catch (Exception ignored) {}
    }

    public static void applyDefaultFont() {
        if (defaultFont == null) return;
        UIManager.put("defaultFont", defaultFont);
    }

    private static Font loadUbuntuNerdFromResources() {
        String[] candidates = new String[] {
                "/fonts/UbuntuNerdFont-Regular.ttf",
                "/fonts/Ubuntu Nerd Font Regular.ttf",
                "/fonts/UbuntuNerdFontMono-Regular.ttf"
        };
        for (String path : candidates) {
            try (InputStream is = FontManager.class.getResourceAsStream(path)) {
                if (is == null) continue;
                Font f = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static Font findUbuntuNerdInstalled() {
        // Try by common family names
        String[] names = new String[] {
                "Ubuntu Nerd Font",
                "Ubuntu Nerd Font Mono",
                "UbuntuMono Nerd Font",
                "Ubuntu Mono Nerd Font",
                "Ubuntu"
        };
        for (String n : names) {
            Font f = new Font(n, Font.PLAIN, 13);
            if (!isLogicalDialogFont(f)) return f;
        }
        // Scan all fonts for a match
        for (Font f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
            String fam = f.getFamily(Locale.ROOT);
            String name = f.getFontName(Locale.ROOT);
            if (fam.toLowerCase(Locale.ROOT).contains("ubuntu") && name.toLowerCase(Locale.ROOT).contains("nerd")) {
                return f;
            }
        }
        return null;
    }

    private static boolean isLogicalDialogFont(Font f) {
        String fam = f.getFamily(Locale.ROOT);
        return fam.equalsIgnoreCase("Dialog") || fam.equalsIgnoreCase("SansSerif") || fam.equalsIgnoreCase("Serif");
    }
}
