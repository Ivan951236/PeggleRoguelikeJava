package com.peggle;

import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import javax.swing.*;
import java.awt.*;

public class MaterialIcons {
    public static Icon outlined(Material2OutlinedAL glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }
    public static Icon filled(Material2AL glyph, int size, Color color) {
        return FontIcon.of(glyph, size, color);
    }
}
