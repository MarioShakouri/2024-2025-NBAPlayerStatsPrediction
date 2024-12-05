import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Player {
    String name;
    double totalPoints;
    double totalAssists;
    double totalRebounds;
    double totalSteals;
    double totalBlocks;
    int gamesPlayed;

    public Player(String name) {
        this.name = name;
        this.totalPoints = 0;
        this.totalAssists = 0;
        this.totalRebounds = 0;
        this.totalSteals = 0;
        this.totalBlocks = 0;
        this.gamesPlayed = 0;
    }

    // Projected stats based on 82-game season
    public double getProjectedPoints() {
        return (gamesPlayed > 0) ? (totalPoints / gamesPlayed) * 82 : 0;
    }

    public double getProjectedAssists() {
        return (gamesPlayed > 0) ? (totalAssists / gamesPlayed) * 82 : 0;
    }

    public double getProjectedRebounds() {
        return (gamesPlayed > 0) ? (totalRebounds / gamesPlayed) * 82 : 0;
    }

    public double getProjectedSteals() {
        return (gamesPlayed > 0) ? (totalSteals / gamesPlayed) * 82 : 0;
    }

    public double getProjectedBlocks() {
        return (gamesPlayed > 0) ? (totalBlocks / gamesPlayed) * 82 : 0;
    }

    public double calculateMVPScore() {
        return (0.4 * getProjectedPoints()) + (0.2 * getProjectedAssists()) +
                (0.2 * getProjectedRebounds()) + (0.1 * getProjectedSteals()) +
                (0.1 * getProjectedBlocks());
    }
}

public class MVPPrediction {
    public static void main(String[] args) {
        String filePath = "database_24_25.csv";
        Map<String, Player> playerStats = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] values = line.split(",");

                String name = values[0].trim();
                double points = Double.parseDouble(values[22]);
                double assists = Double.parseDouble(values[17]);
                double defensiveRebounds = Double.parseDouble(values[15]);
                double offensiveRebounds = Double.parseDouble(values[14]);
                double steals = Double.parseDouble(values[18]);
                double blocks = Double.parseDouble(values[19]);

                double rebounds = defensiveRebounds + offensiveRebounds;

                Player player = playerStats.getOrDefault(name, new Player(name));
                player.totalPoints += points;
                player.totalAssists += assists;
                player.totalRebounds += rebounds;
                player.totalSteals += steals;
                player.totalBlocks += blocks;
                player.gamesPlayed += 1;

                playerStats.put(name, player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Player> players = new ArrayList<>(playerStats.values());
        players.sort((p1, p2) -> Double.compare(p2.calculateMVPScore(), p1.calculateMVPScore())); // Sort descending by MVP score

        System.out.println("Top 5 Players for MVP:");
        for (int i = 0; i < Math.min(5, players.size()); i++) {
            Player player = players.get(i);
            double score = player.calculateMVPScore();
            System.out.println((i + 1) + ". " + player.name + " - MVP Score: " + score);
        }

        if (!players.isEmpty()) {
            Player mvp = players.get(0);
            System.out.println("\nPredicted MVP: " + mvp.name + " with an MVP Score of " + mvp.calculateMVPScore());
        } else {
            System.out.println("\nNo MVP could be determined.");
        }
    }
}
