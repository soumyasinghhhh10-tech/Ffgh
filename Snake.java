import java.awt.Point;
import java.util.LinkedList;

// One snake on the board. The head is always the FIRST point in the list.
public class Snake {

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;

    private final LinkedList<Point> body = new LinkedList<>();
    private int direction;      // direction used in the last move
    private int nextDirection;  // direction the player wants now
    private int score = 0;

    public Snake(int headX, int headY, int startDir, int length) {
        direction = startDir;
        nextDirection = startDir;

        // the body is built behind the head, so we go opposite to the moving direction
        for (int i = 0; i < length; i++) {
            body.add(new Point(headX - dx(startDir) * i, headY - dy(startDir) * i));
        }
    }

    private static int dx(int dir) {
        if (dir == LEFT) return -1;
        if (dir == RIGHT) return 1;
        return 0;
    }

    private static int dy(int dir) {
        if (dir == UP) return -1;
        if (dir == DOWN) return 1;
        return 0;
    }

    private static boolean isOpposite(int a, int b) {
        return (a == UP && b == DOWN) || (a == DOWN && b == UP)
            || (a == LEFT && b == RIGHT) || (a == RIGHT && b == LEFT);
    }

    // called when the player presses a key
    public void turn(int newDir) {
        // a snake can't turn back on itself, otherwise it would die instantly
        if (isOpposite(newDir, direction)) {
            return;
        }
        nextDirection = newDir;
    }

    // where the head will be after the next move (we don't move yet)
    public Point nextHead() {
        Point head = body.getFirst();
        return new Point(head.x + dx(nextDirection), head.y + dy(nextDirection));
    }

    // grow = true means it just ate food, so we keep the tail
    public void move(boolean grow) {
        body.addFirst(nextHead());
        direction = nextDirection;
        if (!grow) {
            body.removeLast();
        }
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        score += points;
    }
}
