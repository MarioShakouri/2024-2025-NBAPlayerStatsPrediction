import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Scorer {
    String name;
    double totalPoints;
    int gamesPlayed;

    public Scorer(String name, double totalPoints, int gamesPlayed) {
        this.name = name;
        this.totalPoints = totalPoints;
        this.gamesPlayed = gamesPlayed;
    }

    public double calculateProjectedPoints() {
        double averagePPG = gamesPlayed > 0 ? totalPoints / gamesPlayed : 0;
        return averagePPG * 82;
    }
}

public class TopScorers {
    public static void main(String[] args) {
        String filePath = "database_24_25.csv";
        Map<String, Scorer> playerStats = new HashMap<>();

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
                double points = 0;
                try {
                    points = Double.parseDouble(values[22]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid points value for player: " + name);
                }

                Scorer scorer = playerStats.getOrDefault(name, new Scorer(name, 0, 0));
                scorer.totalPoints += points;
                scorer.gamesPlayed += 1;
                playerStats.put(name, scorer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Scorer> players = new ArrayList<>(playerStats.values());
        players.sort((p1, p2) -> Double.compare(p2.calculateProjectedPoints(), p1.calculateProjectedPoints()));

        System.out.println("Top 5 Players in Projected Total Points:");
        for (int i = 0; i < Math.min(5, players.size()); i++) {
            Scorer player = players.get(i);
            double projectedPoints = player.calculateProjectedPoints();
            System.out.println((i + 1) + ". " + player.name + " - Projected Points: " + projectedPoints);
        }
    }
}




