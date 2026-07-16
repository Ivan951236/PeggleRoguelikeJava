# Peggle Roguelike Preset Generator

It is a application that generates an preset for the Roguelike Peggle mode, an unofficial mode to rely on fully rules, it is not a mod to Peggle.

## Features
- Random preset generation for inventory, Peggle levels, and boss levels
- Theme switching between Light and Dark modes
- Tabbed interface with three sections:
  - Inventory: Shows 3 random inventory items
  - Peggle Levels: Shows 15 random level-inventory combinations in an 8x2 grid
  - Boss Level: Shows 1 random boss-inventory combination

## Building and Running

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
