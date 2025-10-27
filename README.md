# Peggle Roguelike Preset Generator

This is a Java Swing application converted from a C++ Qt6 application. It generates random presets for a Peggle Roguelike game.

## Original Language
**C++** with Qt6 framework

## Converted to
**Java** with Swing GUI framework

## Features
- Random preset generation for inventory, Peggle levels, and boss levels
- Theme switching between Light and Dark modes
- Tabbed interface with three sections:
  - Inventory: Shows 3 random inventory items
  - Peggle Levels: Shows 15 random level-inventory combinations in an 8x2 grid
  - Boss Level: Shows 1 random boss-inventory combination

## Building and Running

### Optional: Bundle Ubuntu Nerd Font
- Download "Ubuntu Nerd Font" from Nerd Fonts.
- Put the TTF at `src/main/resources/fonts/UbuntuNerdFont-Regular.ttf` (or `Ubuntu Nerd Font Regular.ttf`).
- The app will auto-load it and use it as the global font; if not present, it will try an installed system font.

### Requirements
- Java 11 or higher
- Gradle (included via wrapper)

### Build Commands
```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

# Or use the custom run task
./gradlew runApp

# Create executable JAR
./gradlew jar
```

### Manual Java Execution
If you prefer to run with plain Java:
```bash
# Compile
javac -d build/classes src/main/java/com/peggle/*.java

# Run
java -cp build/classes com.peggle.Main
```

## Project Structure
```
src/main/java/com/peggle/
├── Main.java           # Application entry point
├── MainWindow.java     # Main GUI window (converted from mainwindow.h/cpp)
└── ThemeManager.java   # Theme management (converted from thememanager.h/cpp)
```

## Conversion Notes
- Qt6 widgets converted to Swing equivalents:
  - QMainWindow → JFrame
  - QTabWidget → JTabbedPane
  - QPushButton → JButton
  - QLabel → JLabel
  - QGridLayout → GridLayout
- Qt's signal-slot system converted to Java ActionListener pattern
- QRandomGenerator converted to java.util.Random
- Qt theme system converted to custom Color management