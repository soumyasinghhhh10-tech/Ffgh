import java.awt.Point;
import java.util.Random;

// All the game rules live here. Only the HOST runs this class.
// The client never calculates anything, it only shows what the host sends.
public class GameEngine {

    public static final int COLS = 30;
    public static final int ROWS = 24;
    public static final int TICK_MS = 120;        // one game step every 120 ms
    public static final int GAME_SECONDS = 60;
    private static final int COUNTDOWN_SECONDS = 3;
    private static final int FOOD_POINTS = 10;

    private Snake snake1;
    private Snake snake2;
    private Point food = new Point(-1, -1);
    private String status = "WAITING";
    private String message = "";
    private String name1 = "Player 1";
    private String name2 = "Player 2";
    private int ticksPlayed = 0;
    private int countdownTicks = 0;
    private final Random random = new Random();

    public void setNames(String n1, String n2) {
        name1 = n1;
        name2 = n2;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public boolean isOver() {
        return status.equals("OVER");
    }

    // starts a fresh round (also used for restart)
    public void start() {
        snake1 = new Snake(5, ROWS / 2, Snake.RIGHT, 4);
        snake2 = new Snake(COLS - 6, ROWS / 2, Snake.LEFT, 4);
        ticksPlayed = 0;
        countdownTicks = COUNTDOWN_SECONDS * 1000 / TICK_MS;
        placeFood();
        status = "COUNTDOWN";
        message = String.valueOf(COUNTDOWN_SECONDS);
    }

    // player is 1 (host) or 2 (client)
    public void turn(int player, int dir) {
        if (snake1 == null || isOver()) {
            return;
        }
        if (player == 1) {
            snake1.turn(dir);
        } else {
            snake2.turn(dir);
        }
    }

    // used when the other player leaves in the middle of the game
    public void abort(String reason) {
        status = "OVER";
        message = reason;
    }

    // called again and again by the host's timer
    public void tick() {
        if (status.equals("COUNTDOWN")) {
            countdownTicks--;
            if (countdownTicks <= 0) {
                status = "RUNNING";
                message = "";
            } else {
                message = String.valueOf((countdownTicks * TICK_MS + 999) / 1000);
            }
            return;
        }
        if (!status.equals("RUNNING")) {
            return;
        }

        ticksPlayed++;

        // look where both heads WILL go
        Point head1 = snake1.nextHead();
        Point head2 = snake2.nextHead();
        boolean eat1 = head1.equals(food);
        boolean eat2 = head2.equals(food);

        boolean dead1 = hitWall(head1) || hitSnake(head1, snake1, eat1) || hitSnake(head1, snake2, eat2);
        boolean dead2 = hitWall(head2) || hitSnake(head2, snake1, eat1) || hitSnake(head2, snake2, eat2);

        // both heads in the same cell = both crash
        if (head1.equals(head2)) {
            dead1 = true;
            dead2 = true;
        }

        if (dead1 || dead2) {
            crash(dead1, dead2);
            return;
        }

        snake1.move(eat1);
        snake2.move(eat2);
        if (eat1) snake1.addScore(FOOD_POINTS);
        if (eat2) snake2.addScore(FOOD_POINTS);
        if (eat1 || eat2) placeFood();

        if (getTimeLeft() <= 0) {
            timeUp();
        }
    }

    private boolean hitWall(Point p) {
        return p.x < 0 || p.y < 0 || p.x >= COLS || p.y >= ROWS;
    }

    // does point p touch any part of snake s?
    // if the snake is not growing, its tail moves away this turn, so we skip it
    private boolean hitSnake(Point p, Snake s, boolean growing) {
        int size = s.getBody().size();
        int i = 0;
        for (Point part : s.getBody()) {
            boolean isTail = (i == size - 1);
            if (!(isTail && !growing) && part.equals(p)) {
                return true;
            }
            i++;
        }
        return false;
    }

    private void crash(boolean dead1, boolean dead2) {
        status = "OVER";
        if (dead1 && dead2) {
            message = "Head-on crash! It's a draw.";
        } else if (dead1) {
            message = name2 + " wins! " + name1 + " crashed.";
        } else {
            message = name1 + " wins! " + name2 + " crashed.";
        }
    }

    private void timeUp() {
        status = "OVER";
        int s1 = snake1.getScore();
        int s2 = snake2.getScore();
        if (s1 > s2) {
            message = "Time's up! " + name1 + " wins!";
        } else if (s2 > s1) {
            message = "Time's up! " + name2 + " wins!";
        } else {
            message = "Time's up! It's a draw.";
        }
    }

    public int getTimeLeft() {
        int left = GAME_SECONDS - ticksPlayed * TICK_MS / 1000;
        return Math.max(left, 0);
    }

    // keep picking random cells until we find one that is empty
    private void placeFood() {
        Point p;
        do {
            p = new Point(random.nextInt(COLS), random.nextInt(ROWS));
        } while (snake1.getBody().contains(p) || snake2.getBody().contains(p));
        food = p;
    }

    public GameState getState() {
        GameState gs = new GameState();
        gs.status = status;
        gs.message = message;
        gs.name1 = name1;
        gs.name2 = name2;
        gs.food = food;
        gs.timeLeft = getTimeLeft();
        if (snake1 != null) {
            gs.snake1.addAll(snake1.getBody());
            gs.snake2.addAll(snake2.getBody());
            gs.score1 = snake1.getScore();
            gs.score2 = snake2.getScore();
        }
        return gs;
    }
}
