import java.io.*;

// Saves the best score of this computer in a small text file (file handling).
public class HighScore {

    private static final File FILE =
            new File(System.getProperty("user.home"), ".neonsnake_highscore.txt");

    public static int load() {
        if (!FILE.exists()) {
            return 0;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            return Integer.parseInt(br.readLine().trim());
        } catch (Exception e) {
            return 0;   // file empty or damaged, just start from 0
        }
    }

    // returns true if this score is a new record
    public static boolean submit(int score) {
        if (score <= load()) {
            return false;
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println(score);
        } catch (IOException e) {
            System.out.println("Could not save high score: " + e.getMessage());
        }
        return true;
    }
}
