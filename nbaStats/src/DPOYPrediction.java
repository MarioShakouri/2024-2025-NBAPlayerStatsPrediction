import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class DefensivePlayer {
    String name;
    double totalDefensiveRebounds;
    double totalSteals;
    double totalBlocks;
    int gamesPlayed;

    public DefensivePlayer(String name) {
        this.name = name;
        this.totalDefensiveRebounds = 0;
        this.totalSteals = 0;
        this.totalBlocks = 0;
        this.gamesPlayed = 0;
    }

    public double getProjectedDefensiveRebounds() {
        return (gamesPlayed > 0) ? (totalDefensiveRebounds / gamesPlayed) * 82 : 0;
    }

    public double getProjectedSteals() {
        return (gamesPlayed > 0) ? (totalSteals / gamesPlayed) * 82 : 0;
    }

    public double getProjectedBlocks() {
        return (gamesPlayed > 0) ? (totalBlocks / gamesPlayed) * 82 : 0;
    }

    public double calculateDefensiveScore() {
        return (0.3 * getProjectedDefensiveRebounds()) + (0.4 * getProjectedSteals()) + (0.3 * getProjectedBlocks());
    }
}

public class DPOYPrediction {
    public static void main(String[] args) {
        String filePath = "database_24_25.csv";
        Map<String, DefensivePlayer> playerStats = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",");

                String name = values[0];
                double defensiveRebounds = Double.parseDouble(values[15]);
                double steals = Double.parseDouble(values[18]);
                double blocks = Double.parseDouble(values[19]);

                DefensivePlayer player = playerStats.getOrDefault(name, new DefensivePlayer(name));
                player.totalDefensiveRebounds += defensiveRebounds;
                player.totalSteals += steals;
                player.totalBlocks += blocks;
                player.gamesPlayed += 1;

                playerStats.put(name, player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<DefensivePlayer> players = new ArrayList<>(playerStats.values());
        players.removeIf(player -> player.gamesPlayed < 10); // Exclude players with fewer than 10 games for fairness
        players.sort((p1, p2) -> Double.compare(p2.calculateDefensiveScore(), p1.calculateDefensiveScore()));

        System.out.println("Top 5 Players for Defensive Player of the Year:");
        for (int i = 0; i < Math.min(5, players.size()); i++) {
            DefensivePlayer player = players.get(i);
            double score = player.calculateDefensiveScore();
            System.out.println((i + 1) + ". " + player.name + " - Defensive Score: " + score);
        }

        if (!players.isEmpty()) {
            DefensivePlayer dpoy = players.get(0);
            System.out.println("\nPredicted Defensive Player of the Year: " + dpoy.name + " with a Defensive Score of " + dpoy.calculateDefensiveScore());
        } else {
            System.out.println("\nNo Defensive Player of the Year could be determined.");
        }
    }
}

