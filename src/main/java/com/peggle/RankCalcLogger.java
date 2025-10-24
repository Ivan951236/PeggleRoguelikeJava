package com.peggle;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class RankCalcLogger {
    private RankCalcLogger() {}

    public static void writeRankCalculationYaml(int roguelikeWins,
                                                int levelWins,
                                                int clutches,
                                                int totalMisses,
                                                int lostRounds,
                                                int lostOnBossLevel,
                                                int feverHundredKs,
                                                double overall,
                                                String rank) {
        try {
            Path jarDir = getJarDirectory();
            Path outDir = jarDir.resolve("rankCalcs");
            Files.createDirectories(outDir);

            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fileName = "rankcalc_" + ts + ".yaml";
            Path outFile = outDir.resolve(fileName);

            String sessionId = SessionManager.getSessionId();
            String yaml = buildYaml(sessionId,
                                    roguelikeWins, levelWins, clutches, totalMisses,
                                    lostRounds, lostOnBossLevel, feverHundredKs,
                                    overall, rank);
            Files.writeString(outFile, yaml, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            // Intentionally ignore logging errors to not disturb UI flow
        }
    }

    private static Path getJarDirectory() throws URISyntaxException {
        Path codeSource = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isRegularFile(codeSource)) {
            // Running from a JAR: the JAR file is the codeSource, so use its parent directory
            return codeSource.getParent();
        }
        // Running from classes directory; use that directory as base
        return codeSource;
    }

    private static String buildYaml(String sessionId,
                                    int roguelikeWins,
                                    int levelWins,
                                    int clutches,
                                    int totalMisses,
                                    int lostRounds,
                                    int lostOnBossLevel,
                                    int feverHundredKs,
                                    double overall,
                                    String rank) {
        StringBuilder sb = new StringBuilder();
        sb.append("session_id: ").append(singleQuote(sessionId)).append('\n');
        sb.append("timestamp: ").append(singleQuote(LocalDateTime.now().toString())).append('\n');
        sb.append("inputs:\n");
        sb.append("  roguelikeWins: ").append(roguelikeWins).append('\n');
        sb.append("  levelWins: ").append(levelWins).append('\n');
        sb.append("  clutches: ").append(clutches).append('\n');
        sb.append("  totalMisses: ").append(totalMisses).append('\n');
        sb.append("  lostRounds: ").append(lostRounds).append('\n');
        sb.append("  lostOnBossLevel: ").append(lostOnBossLevel).append('\n');
        sb.append("  feverHundredKs: ").append(feverHundredKs).append('\n');
        sb.append("results:\n");
        sb.append("  overall: ").append(String.format(java.util.Locale.ROOT, "%.4f", overall)).append('\n');
        sb.append("  rank: ").append(singleQuote(rank)).append('\n');
        return sb.toString();
    }

    private static String singleQuote(String value) {
        if (value == null) return "''";
        // Escape single quotes for YAML by doubling them
        String escaped = value.replace("'", "''");
        return "'" + escaped + "'";
    }
}
