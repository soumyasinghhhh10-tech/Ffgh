import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

// A snapshot of the whole game at one moment.
// The host makes it, turns it into text, and sends it to the other player.
// The client reads the text back into a GameState and just draws it.
public class GameState {

    public String status = "WAITING";   // WAITING, COUNTDOWN, RUNNING, OVER
    public String message = "";
    public String name1 = "Player 1";
    public String name2 = "Player 2";
    public int score1 = 0;
    public int score2 = 0;
    public int timeLeft = GameEngine.GAME_SECONDS;
    public Point food = new Point(-1, -1);
    public List<Point> snake1 = new ArrayList<>();
    public List<Point> snake2 = new ArrayList<>();

    // format: status|time|score1|score2|foodX,foodY|name1|name2|body1|body2|message
    public String toText() {
        return status + "|" + timeLeft + "|" + score1 + "|" + score2 + "|"
                + food.x + "," + food.y + "|" + name1 + "|" + name2 + "|"
                + bodyToText(snake1) + "|" + bodyToText(snake2) + "|" + message;
    }

    public static GameState fromText(String text) {
        String[] parts = text.split("\\|", -1);
        GameState gs = new GameState();
        gs.status = parts[0];
        gs.timeLeft = Integer.parseInt(parts[1]);
        gs.score1 = Integer.parseInt(parts[2]);
        gs.score2 = Integer.parseInt(parts[3]);

        String[] f = parts[4].split(",");
        gs.food = new Point(Integer.parseInt(f[0]), Integer.parseInt(f[1]));

        gs.name1 = parts[5];
        gs.name2 = parts[6];
        gs.snake1 = textToBody(parts[7]);
        gs.snake2 = textToBody(parts[8]);
        gs.message = parts[9];
        return gs;
    }

    private static String bodyToText(List<Point> body) {
        StringBuilder sb = new StringBuilder();
        for (Point p : body) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(p.x).append(",").append(p.y);
        }
        return sb.toString();
    }

    private static List<Point> textToBody(String text) {
        List<Point> body = new ArrayList<>();
        if (text.isEmpty()) {
            return body;
        }
        for (String cell : text.split(";")) {
            String[] xy = cell.split(",");
            body.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        return body;
    }
}
