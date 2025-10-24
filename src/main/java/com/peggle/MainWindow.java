package com.peggle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

public class MainWindow extends JFrame implements ThemeManager.ThemeChangeListener {
    static {
        // Material-ish component metrics via FlatLaf defaults
        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 20);
        UIManager.put("TextComponent.arc", 14);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.tabsOverlapBorder", true);
    }
    
    private JTabbedPane tabbedPane;
    private JButton generateButton;
    private JButton themeButton;
    private JPanel inventoryPanel;
    private JPanel inventoryPanelP1;
    private JPanel inventoryPanelP2;
    private JPanel peggleLevelsPanel;
    private JPanel bossLevelPanel;
    private JPanel levelsPanel;

    private JPanel modePanel;
    private JRadioButton singleplayerRadio;
    private JRadioButton duelRadio;
    private JTextField player1Field;
    private JTextField player2Field;
    private JComboBox<String> variantCombo;
    private JButton themeModeToggle;

    private JTextField tfRoguelikeWins;
    private JTextField tfLevelWins;
    private JTextField tfClutches;
    private JTextField tfTotalMisses;
    private JTextField tfLostRounds;
    private JTextField tfLostOnBoss;
    private JTextField tfFeverHundredKs;
    private JButton calcRankButton;
    private JLabel lblOverall;
    private JLabel lblRank;
    
    private Random random;
    private ThemeManager themeManager;
    
    private List<String> inventoryNames;
    private List<String> levelNames;
    private List<String> bossNames;
    
    public MainWindow() {
        super("Peggle Roguelike Preset Generator");
        
        random = new Random();
        themeManager = ThemeManager.getInstance();
        themeManager.addThemeChangeListener(this);
        
        setupNameMaps();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadConfigIntoUI();
        applyTheme();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);
    }
    
    private void setupNameMaps() {
        // Inventory names (1-10)
        inventoryNames = new ArrayList<>();
        inventoryNames.add(""); // index 0 - empty
        inventoryNames.add("Bjorn");
        inventoryNames.add("Jimmy Lighting");
        inventoryNames.add("Kat Tut");
        inventoryNames.add("Spork");
        inventoryNames.add("Claude");
        inventoryNames.add("Reinfield");
        inventoryNames.add("Tula");
        inventoryNames.add("Warren");
        inventoryNames.add("Lord Cinderbottom");
        inventoryNames.add("Master Hu");
        
        // Level names (1-50)
        String[] levels = {
            "Peggleland", "Slip and Slide", "Bjorn's Gazebo",
            "Das Bucket", "Snow Day", "Birdy's Crib",
            "Buffalo Wings", "Skate Park", "Spiral of Doom",
            "Mr. Peepers", "Scarab Crunch", "Infinite Cheese",
            "Ra Deal", "Croco-Gator Pit", "The Fever Level",
            "The Amoeban", "The Last Flower", "We Come In Peace",
            "Maid In Space", "Getting The Spare", "Pearl Clam",
            "Insane Aquarium", "Tasty Waves", "Our Favorite Eel",
            "Love Story", "Waves", "Spiderweb", "Blockers",
            "Baseball", "Vermin", "Holland Oats", "I Heart Flowers",
            "Workin From Home", "Tula's Ride", "70 and Sunny",
            "Win a Monkey", "Dog Pinball", "Spin Again",
            "Roll 'em", "Five of a Kind", "The Love Moat",
            "Doom with a View", "Rhombi", "9 Luft Ballons",
            "Twister Sisters", "Spin Cycle", "The Dude Abides",
            "When Pigs Fly", "Yang, Yin", "Zen Frog"
        };
        
        levelNames = new ArrayList<>();
        levelNames.add(""); // index 0 - empty
        for (String level : levels) {
            levelNames.add(level);
        }
        
        // Boss names (51-55)
        bossNames = new ArrayList<>();
        for (int i = 0; i <= 55; i++) {
            bossNames.add(""); // Fill with empty strings
        }
        bossNames.set(51, "Paw Reader");
        bossNames.set(52, "End of Time");
        bossNames.set(53, "Billions & Billions");
        bossNames.set(54, "Don't Panic");
        bossNames.set(55, "Beyond Reason");
    }
    
private void initializeComponents() {
        // Create main components
        tabbedPane = new JTabbedPane();
        // Underlined tabs for MD3-ish look
        tabbedPane.putClientProperty("JTabbedPane.tabType", "underlined");
        tabbedPane.putClientProperty("JTabbedPane.showContentSeparator", Boolean.FALSE);

        generateButton = new JButton("Generate");
        themeButton = new JButton("Toggle Dark Mode");

        // Mode selector
        modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        singleplayerRadio = new JRadioButton("Singleplayer", true);
        duelRadio = new JRadioButton("Duel");
        ButtonGroup group = new ButtonGroup();
        group.add(singleplayerRadio);
        group.add(duelRadio);
        player1Field = new JTextField(10);
        player2Field = new JTextField(10);

        // Placeholders and clear buttons
        player1Field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Player 1");
        player2Field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Player 2");
        player1Field.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        player2Field.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        variantCombo = new JComboBox<>();
        themeModeToggle = new JButton("Theme");
        modePanel.add(new JLabel("Mode:"));
        modePanel.add(singleplayerRadio);
        modePanel.add(duelRadio);
        modePanel.add(new JLabel("P1 Name:"));
        modePanel.add(player1Field);
        modePanel.add(new JLabel("P2 Name:"));
        modePanel.add(player2Field);
        modePanel.add(new JLabel("Variant:"));
        modePanel.add(variantCombo);
        modePanel.add(themeModeToggle);

        // Button shapes
        generateButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        themeButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        themeModeToggle.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        
        // Create panels for tabs
        inventoryPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inventoryPanelP1 = new JPanel(new GridLayout(1, 3, 10, 10));
        inventoryPanelP2 = new JPanel(new GridLayout(1, 3, 10, 10));
        inventoryPanel.add(inventoryPanelP1);
        inventoryPanel.add(inventoryPanelP2);

        peggleLevelsPanel = new JPanel(new GridLayout(2, 8, 5, 5));
        bossLevelPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        levelsPanel = new JPanel();
        levelsPanel.setLayout(new GridBagLayout());
        buildLevelsPanel(levelsPanel);
        
        // Add padding to panels
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inventoryPanelP1.setBorder(BorderFactory.createTitledBorder("Player 1"));
        inventoryPanelP2.setBorder(BorderFactory.createTitledBorder("Player 2"));
        peggleLevelsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bossLevelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        levelsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add tabs with icons
        ThemeManager tm = themeManager;
        Color iconColor = tm.getTextColor();
        tabbedPane.addTab("Inventory", MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.ADD_SHOPPING_CART, 16, iconColor), inventoryPanel);
        tabbedPane.addTab("Peggle Levels", MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.GRID_ON, 16, iconColor), peggleLevelsPanel);
        tabbedPane.addTab("Boss Level", MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.LOCAL_FIRE_DEPARTMENT, 16, iconColor), bossLevelPanel);
        tabbedPane.addTab("Levels", MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.INSIGHTS, 16, iconColor), levelsPanel);
    }
    
private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(generateButton);
        buttonPanel.add(themeButton);
        
        // Main layout
        add(modePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
private void setupEventListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onGenerateClicked();
            }
        });
        
        themeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onThemeToggleClicked();
            }
        });

        themeModeToggle.addActionListener(e -> onThemeToggleClicked());
        
        singleplayerRadio.addActionListener(e -> {
            updateModeUI();
            saveModeToConfig();
        });
        duelRadio.addActionListener(e -> {
            updateModeUI();
            saveModeToConfig();
        });

        DocumentListener nameListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { saveNamesToConfig(); }
            @Override public void removeUpdate(DocumentEvent e) { saveNamesToConfig(); }
            @Override public void changedUpdate(DocumentEvent e) { saveNamesToConfig(); }
        };
        player1Field.getDocument().addDocumentListener(nameListener);
        player2Field.getDocument().addDocumentListener(nameListener);

        // Fill variants and react to change
        variantCombo.removeAllItems();
        for (String v : themeManager.availableVariants()) variantCombo.addItem(v);
        variantCombo.setSelectedItem(themeManager.getCurrentVariant());
        variantCombo.addActionListener(e -> {
            String sel = (String) variantCombo.getSelectedItem();
            if (sel != null) {
                themeManager.setVariant(sel);
            }
        });
    }
    
private void onGenerateClicked() {
        clearPanels();

        // Inventory: singleplayer or duel
        if (isDuel()) {
            generateInventoryForPanel(inventoryPanelP1);
            generateInventoryForPanel(inventoryPanelP2);
        } else {
            generateInventoryForPanel(inventoryPanelP1);
        }
        
        // Generate peggle levels (15 items, 8x2 grid)
        for (int i = 0; i < 15; i++) {
            int levelIndex = random.nextInt(levelNames.size() - 1) + 1; // Skip index 0
            int invIndex = random.nextInt(inventoryNames.size() - 1) + 1; // Skip index 0
            String text = levelNames.get(levelIndex) + ", " + inventoryNames.get(invIndex);
            JLabel label = createStyledLabel(text);
            peggleLevelsPanel.add(label);
        }
        
        // Generate boss level (1 item)
        int bossIndex = random.nextInt(5) + 51; // Random between 51-55
        int invIndex = random.nextInt(inventoryNames.size() - 1) + 1; // Skip index 0
        String text = bossNames.get(bossIndex) + ", " + inventoryNames.get(invIndex);
        JLabel label = createStyledLabel(text);
        bossLevelPanel.add(label);
        
        // Save config after generation
        saveAllToConfig();

        // Refresh the display
        revalidate();
        repaint();
    }
    
    private void onThemeToggleClicked() {
        ThemeManager.Theme currentTheme = themeManager.getCurrentTheme();
        ThemeManager.Theme newTheme = (currentTheme == ThemeManager.Theme.LIGHT) ? 
                                      ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT;
        themeManager.setTheme(newTheme);
        ConfigManager cfg = ConfigManager.getInstance();
        cfg.set("themeMode", newTheme.name());
        cfg.set("themeVariant", themeManager.getCurrentVariant());
        cfg.save();
    }
    
private void clearPanels() {
        inventoryPanelP1.removeAll();
        inventoryPanelP2.removeAll();
        peggleLevelsPanel.removeAll();
        bossLevelPanel.removeAll();
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(themeManager.getForegroundColor());
        label.setForeground(themeManager.getTextColor());
        label.setBorder(new LineBorder(themeManager.getOutlineColor(), 2));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        return label;
    }
    
    @Override
    public void onThemeChanged(ThemeManager.Theme theme) {
        // Update LAF when theme mode changes with a smooth transition
        try {
            FlatAnimatedLafChange.showSnapshot();
            if (theme == ThemeManager.Theme.DARK) {
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            } else {
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            }
            // Reapply default font after LAF change
            FontManager.applyDefaultFont();
            javax.swing.SwingUtilities.updateComponentTreeUI(this);
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        } catch (Exception ignored) {}
        applyTheme();
    }
    
private void applyTheme() {
        // Update button text and tab icons color
        if (themeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT) {
            themeButton.setText("Dark Mode");
            themeModeToggle.setText("Dark Mode");
        } else {
            themeButton.setText("Light Mode");
            themeModeToggle.setText("Light Mode");
        }
        
        // Apply colors to main components
        Color backgroundColor = themeManager.getBackgroundColor();
        Color surfaceColor = themeManager.getSurfaceColor();
        Color textColor = themeManager.getTextColor();
        Color buttonColor = themeManager.getButtonColor();
        Color onPrimary = themeManager.getOnPrimaryColor();
        Color outlineColor = themeManager.getOutlineColor();
        
        // Main window
        getContentPane().setBackground(surfaceColor);
        
        // Top mode panel controls
        modePanel.setBackground(surfaceColor);
        for (Component c : modePanel.getComponents()) {
            c.setBackground(surfaceColor);
            c.setForeground(textColor);
        }

        // Tabbed pane
        tabbedPane.setBackground(surfaceColor);
        tabbedPane.setForeground(textColor);
        // Underline color to primary
        tabbedPane.putClientProperty("JTabbedPane.underlineColor", buttonColor);
        
        // Panels and components (recursive)
        applyColorsToContainer(inventoryPanel, surfaceColor, textColor, outlineColor);
        applyColorsToContainer(peggleLevelsPanel, surfaceColor, textColor, outlineColor);
        applyColorsToContainer(bossLevelPanel, surfaceColor, textColor, outlineColor);
        applyColorsToContainer(levelsPanel, surfaceColor, textColor, outlineColor);

        // Titled borders for inventory panels (ensure title text color)
        ((javax.swing.border.TitledBorder)inventoryPanelP1.getBorder()).setTitleColor(textColor);
        ((javax.swing.border.TitledBorder)inventoryPanelP2.getBorder()).setTitleColor(textColor);

        // Buttons
        generateButton.setBackground(buttonColor);
        generateButton.setForeground(onPrimary);
        generateButton.setBorder(new LineBorder(outlineColor, 0));
        generateButton.setIcon(MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.CACHED, 16, themeManager.getTextColor()));
        
        themeButton.setBackground(buttonColor);
        themeButton.setForeground(onPrimary);
        themeButton.setBorder(new LineBorder(outlineColor, 0));
        themeButton.setIcon(MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.BEDTIME, 16, themeManager.getTextColor()));

        themeModeToggle.setBackground(buttonColor);
        themeModeToggle.setForeground(onPrimary);
        themeModeToggle.setBorder(new LineBorder(outlineColor, 0));
        themeModeToggle.setIcon(MaterialIcons.outlined(
                themeManager.getCurrentTheme() == ThemeManager.Theme.DARK ? org.kordamp.ikonli.material2.Material2OutlinedAL.LIGHTBULB : org.kordamp.ikonli.material2.Material2OutlinedAL.BEDTIME,
                16, themeManager.getTextColor()));
        
        if (calcRankButton != null) {
            calcRankButton.setBackground(buttonColor);
            calcRankButton.setForeground(onPrimary);
            calcRankButton.setBorder(new LineBorder(outlineColor, 0));
            calcRankButton.setIcon(MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.INSIGHTS, 16, themeManager.getTextColor()));
            calcRankButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        }
        
        // Update tab icons to match new colors
        Color iconColor2 = themeManager.getTextColor();
        if (tabbedPane.getTabCount() >= 4) {
            tabbedPane.setIconAt(0, MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.ADD_SHOPPING_CART, 16, iconColor2));
            tabbedPane.setIconAt(1, MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.GRID_ON, 16, iconColor2));
            tabbedPane.setIconAt(2, MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.LOCAL_FIRE_DEPARTMENT, 16, iconColor2));
            tabbedPane.setIconAt(3, MaterialIcons.outlined(org.kordamp.ikonli.material2.Material2OutlinedAL.INSIGHTS, 16, iconColor2));
        }
        
        // Update existing labels that have custom backgrounds
        updateLabelsInPanel(inventoryPanelP1);
        updateLabelsInPanel(inventoryPanelP2);
        updateLabelsInPanel(peggleLevelsPanel);
        updateLabelsInPanel(bossLevelPanel);
        
        repaint();
    }

    private void applyColorsToContainer(Container container, Color backgroundColor, Color textColor, Color outlineColor) {
        if (container == null) return;
        container.setBackground(backgroundColor);
        for (Component comp : container.getComponents()) {
            // Set foreground for all components
            comp.setForeground(textColor);
            if (comp instanceof JPanel) {
                ((JComponent) comp).setOpaque(true);
                comp.setBackground(backgroundColor);
                applyColorsToContainer((Container) comp, backgroundColor, textColor, outlineColor);
            } else if (comp instanceof JTabbedPane) {
                comp.setBackground(backgroundColor);
                ((JTabbedPane) comp).setForeground(textColor);
                applyColorsToContainer((Container) comp, backgroundColor, textColor, outlineColor);
            } else if (comp instanceof JButton) {
                ((JButton) comp).setForeground(textColor);
            } else if (comp instanceof JRadioButton) {
                ((JRadioButton) comp).setForeground(textColor);
            } else if (comp instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) comp;
                tc.setForeground(textColor);
                tc.setCaretColor(textColor);
                tc.setBackground(backgroundColor);
                if (tc.getBorder() instanceof LineBorder) {
                    tc.setBorder(new LineBorder(outlineColor, 1));
                }
            } else if (comp instanceof JLabel) {
                // Keep label foreground, don't override background for styled labels
                ((JLabel) comp).setForeground(textColor);
            }
        }
    }
    
private void updateLabelsInPanel(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setBackground(themeManager.getForegroundColor());
                label.setForeground(themeManager.getTextColor());
                label.setBorder(new LineBorder(themeManager.getOutlineColor(), 2));
            }
        }
    }

    private boolean isDuel() {
        return duelRadio.isSelected();
    }

    private void updateModeUI() {
        boolean duel = isDuel();
        player2Field.setEnabled(duel);
        ((javax.swing.border.TitledBorder)inventoryPanelP2.getBorder()).setTitle(player2Field.getText());
        ((javax.swing.border.TitledBorder)inventoryPanelP1.getBorder()).setTitle(player1Field.getText());
        inventoryPanel.revalidate();
        inventoryPanel.repaint();
    }

    private void generateInventoryForPanel(JPanel panel) {
        panel.removeAll();
        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(inventoryNames.size() - 1) + 1;
            String name = inventoryNames.get(index);
            JLabel label = createStyledLabel(name);
            panel.add(label);
        }
    }

    private void buildLevelsPanel(JPanel panel) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        tfRoguelikeWins = new JTextField(8);
        tfLevelWins = new JTextField(8);
        tfClutches = new JTextField(8);
        tfTotalMisses = new JTextField(8);
        tfLostRounds = new JTextField(8);
        tfLostOnBoss = new JTextField(8);
        tfFeverHundredKs = new JTextField(8);

        // Placeholders and clear buttons for inputs
        tfRoguelikeWins.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfLevelWins.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfClutches.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfTotalMisses.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfLostRounds.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfLostOnBoss.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfFeverHundredKs.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0");
        tfRoguelikeWins.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfLevelWins.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfClutches.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfTotalMisses.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfLostRounds.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfLostOnBoss.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        tfFeverHundredKs.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        calcRankButton = new JButton("Calculate Rank");
        lblOverall = new JLabel("Overall: 0");
        lblRank = new JLabel("Rank: Newbie");

        addRow(panel, gc, r++, "Roguelike Wins", tfRoguelikeWins);
        addRow(panel, gc, r++, "Level Wins", tfLevelWins);
        addRow(panel, gc, r++, "Clutches", tfClutches);
        addRow(panel, gc, r++, "Total Misses", tfTotalMisses);
        addRow(panel, gc, r++, "Lost Rounds", tfLostRounds);
        addRow(panel, gc, r++, "Lost on Boss Level", tfLostOnBoss);
        addRow(panel, gc, r++, "Every 100000 Points in Fever", tfFeverHundredKs);

        gc.gridx = 0; gc.gridy = r; gc.gridwidth = 1; panel.add(calcRankButton, gc);
        gc.gridx = 1; gc.gridy = r; gc.gridwidth = 1; panel.add(lblOverall, gc);
        gc.gridx = 2; gc.gridy = r; gc.gridwidth = 1; panel.add(lblRank, gc);

        calcRankButton.addActionListener(e -> calculateAndShowRank());
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; panel.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1; panel.add(field, gc);
    }

    private void calculateAndShowRank() {
        int rw = parseInt(tfRoguelikeWins.getText());
        int lw = parseInt(tfLevelWins.getText());
        int cl = parseInt(tfClutches.getText());
        int tm = parseInt(tfTotalMisses.getText());
        int lr = parseInt(tfLostRounds.getText());
        int lob = parseInt(tfLostOnBoss.getText());
        int f100 = parseInt(tfFeverHundredKs.getText());

        double overall = RankCalculator.calculateOverall(rw, lw, cl, tm, lr, lob, f100);
        String rank = RankCalculator.rankFor(overall);

        lblOverall.setText("Overall: " + String.format("%.2f", overall));
        lblRank.setText("Rank: " + rank);

        // Write YAML rank calculation log into JAR dir's rankCalcs folder
        RankCalcLogger.writeRankCalculationYaml(rw, lw, cl, tm, lr, lob, f100, overall, rank);

        // Save to config
        ConfigManager cfg = ConfigManager.getInstance();
        cfg.setLevelField("roguelikeWins", rw);
        cfg.setLevelField("levelWins", lw);
        cfg.setLevelField("clutches", cl);
        cfg.setLevelField("totalMisses", tm);
        cfg.setLevelField("lostRounds", lr);
        cfg.setLevelField("lostOnBossLevel", lob);
        cfg.setLevelField("feverHundredKs", f100);
        cfg.save();
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private void loadConfigIntoUI() {
        ConfigManager cfg = ConfigManager.getInstance();
        String mode = String.valueOf(cfg.getConfig().getOrDefault("mode", "singleplayer"));
        singleplayerRadio.setSelected("singleplayer".equalsIgnoreCase(mode));
        duelRadio.setSelected("duel".equalsIgnoreCase(mode));
        player1Field.setText(String.valueOf(cfg.getConfig().getOrDefault("player1Name", "Player 1")));
        player2Field.setText(String.valueOf(cfg.getConfig().getOrDefault("player2Name", "Player 2")));
        tfRoguelikeWins.setText(String.valueOf(cfg.getLevelField("roguelikeWins") != null ? cfg.getLevelField("roguelikeWins") : 0));
        tfLevelWins.setText(String.valueOf(cfg.getLevelField("levelWins") != null ? cfg.getLevelField("levelWins") : 0));
        tfClutches.setText(String.valueOf(cfg.getLevelField("clutches") != null ? cfg.getLevelField("clutches") : 0));
        tfTotalMisses.setText(String.valueOf(cfg.getLevelField("totalMisses") != null ? cfg.getLevelField("totalMisses") : 0));
        tfLostRounds.setText(String.valueOf(cfg.getLevelField("lostRounds") != null ? cfg.getLevelField("lostRounds") : 0));
        tfLostOnBoss.setText(String.valueOf(cfg.getLevelField("lostOnBossLevel") != null ? cfg.getLevelField("lostOnBossLevel") : 0));
        tfFeverHundredKs.setText(String.valueOf(cfg.getLevelField("feverHundredKs") != null ? cfg.getLevelField("feverHundredKs") : 0));
        updateModeUI();

        // Theme from config if saved
        Object themeVal = cfg.getConfig().get("themeMode");
        if (themeVal instanceof String) {
            try {
                ThemeManager.Theme t = ThemeManager.Theme.valueOf(((String) themeVal).toUpperCase());
                themeManager.setTheme(t);
            } catch (Exception ignored) {}
        }
        Object variant = cfg.getConfig().get("themeVariant");
        if (variant instanceof String) {
            themeManager.setVariant((String) variant);
            variantCombo.setSelectedItem((String) variant);
        }
    }

    private void saveModeToConfig() {
        ConfigManager cfg = ConfigManager.getInstance();
        cfg.set("mode", isDuel() ? "duel" : "singleplayer");
        cfg.save();
    }

    private void saveNamesToConfig() {
        ConfigManager cfg = ConfigManager.getInstance();
        cfg.set("player1Name", player1Field.getText());
        cfg.set("player2Name", player2Field.getText());
        ((javax.swing.border.TitledBorder)inventoryPanelP1.getBorder()).setTitle(player1Field.getText());
        ((javax.swing.border.TitledBorder)inventoryPanelP2.getBorder()).setTitle(player2Field.getText());
        inventoryPanel.repaint();
        cfg.save();
    }

    private void saveAllToConfig() {
        saveModeToConfig();
        saveNamesToConfig();
        ConfigManager cfg = ConfigManager.getInstance();
        cfg.set("theme", themeManager.getCurrentTheme().name());
        cfg.save();
    }
}
