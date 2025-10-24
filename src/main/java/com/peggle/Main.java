package com.peggle;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Enable modern window decorations
                FlatLaf.setUseNativeWindowDecorations(true);

                // Set FlatLaf based on saved theme mode
                ConfigManager cfg = ConfigManager.getInstance();
                String mode = String.valueOf(cfg.getConfig().getOrDefault("themeMode", ThemeManager.Theme.LIGHT.name()));
                ThemeManager.Theme t = ThemeManager.Theme.LIGHT;
                try { t = ThemeManager.Theme.valueOf(mode.toUpperCase()); } catch (Exception ignored) {}
                if (t == ThemeManager.Theme.DARK) UIManager.setLookAndFeel(new FlatDarkLaf());
                else UIManager.setLookAndFeel(new FlatLightLaf());

                // Apply Ubuntu Nerd Font if available/resources provided
                FontManager.installUbuntuNerdAsDefault(13);

                MainWindow window = new MainWindow();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
