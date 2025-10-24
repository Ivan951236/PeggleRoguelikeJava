package com.peggle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankCalculator {

    private static class RankEntry {
        final String name;
        final int threshold;
        RankEntry(String name, int threshold) { this.name = name; this.threshold = threshold; }
    }

    private static final List<RankEntry> RANKS = new ArrayList<>();
    static {
        add("Newbie", 0, 23, 46, 55);
        add("Private", 65, 76, 89, 109);
        add("Admiral", 115, 127, 131, 151);
        add("Newbie VIP", 165, 173, 181, 193);
        add("VIP", 195, 221, 251, 278);
        add("Senior VIP", 300, 345, 402, 412);
        add("Demon Newbie", 450, 510, 550, 610);
        add("Demon Private", 650, 699, 510, 789); // as provided (note 510 duplicate)
        add("Demon Admiral", 950, 1001, 1110, 1143);
        add("Demon Newbie VIP", 1150, 1200, 1225, 1300);
        add("Demon VIP", 1350, 1399, 1450, 1549);
        add("Demon Senior VIP", 1550, 1600, 1700, 1888);
        add("Godly Newbie", 2000, 2010, 2020, 2077);
        add("Godly Private", 2255, 2277, 2311, 2560);
        add("Godly Admiral", 2750, 2999, 3009, 3110);
        add("Godly Newbie VIP", 3120, 3200, 3260, 3310);
        add("Godly VIP", 3330, 3350, 3500, 3610);
        add("Godly Senior VIP", 3650, 3750, 3850, 3950);
        add("Developer", 4000, 4100, 4200, 4400);
        add("Kratos of Gaming", 4500, 5000, 7000, 9000);
        add("METAPCs", 10000, 20000, 30000, 50000);
        RANKS.sort(Comparator.comparingInt(r -> r.threshold));
    }

    private static void add(String name, int a, int b, int c, int d) {
        RANKS.add(new RankEntry(name, a));
        RANKS.add(new RankEntry(name, b));
        RANKS.add(new RankEntry(name, c));
        RANKS.add(new RankEntry(name, d));
    }

    public static double calculateOverall(int roguelikeWins,
                                          int levelWins,
                                          int clutches,
                                          int totalMisses,
                                          int lostRounds,
                                          int lostOnBossLevel,
                                          int feverHundredKs) {
        double rw = Math.max(0, roguelikeWins);
        double lw = Math.max(0, levelWins);
        double cl = Math.max(0, clutches);
        double tm = Math.max(0, totalMisses);
        double lr = Math.max(0, lostRounds);
        double lobl = Math.max(0, lostOnBossLevel);
        double fever = Math.max(0, feverHundredKs);

        double base = rw * lw * cl;
        if (tm == 0) tm = 1; // avoid div by zero
        base = base / tm;
        base = base / (lr + 3.0);
        base = base / (lobl + 2.0);
        base = base * (fever + 1.0);
        return base;
    }

    public static String rankFor(double overall) {
        String best = RANKS.get(0).name; // default lowest
        for (RankEntry r : RANKS) {
            if (overall >= r.threshold) {
                best = r.name;
            } else {
                break;
            }
        }
        return best;
    }
}
