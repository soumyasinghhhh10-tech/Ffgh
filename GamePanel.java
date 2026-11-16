import javax.swing.*;
import java.awt.*;
import java.util.List;

// Only draws things. All the real work is done by GameEngine on the host.
public class GamePanel extends JPanel {

    private static final int CELL = 22;
    private GameState state = new GameState();

    public GamePanel() {
        setPreferredSize(new Dimension(GameEngine.COLS * CELL, GameEngine.ROWS * CELL));
        setBackground(new Color(15, 23, 42));
        setFocusable(true);   // needed so the panel can receive key presses
    }

    public void setState(GameState newState) {
        state = newState;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // faint grid
        g.setColor(new Color(30, 41, 59));
        for (int x = 0; x <= GameEngine.COLS; x++) {
            g.drawLine(x * CELL, 0, x * CELL, GameEngine.ROWS * CELL);
        }
        for (int y = 0; y <= GameEngine.ROWS; y++) {
            g.drawLine(0, y * CELL, GameEngine.COLS * CELL, y * CELL);
        }

        // food
        if (state.food.x >= 0) {
            g.setColor(new Color(239, 68, 68));
            g.fillOval(state.food.x * CELL + 3, state.food.y * CELL + 3, CELL - 6, CELL - 6);
        }

        drawSnake(g, state.snake1, new Color(34, 197, 94), new Color(134, 239, 172));
        drawSnake(g, state.snake2, new Color(249, 115, 22), new Color(253, 186, 116));

        // big text in the middle (countdown, waiting, winner...)
        if (!state.message.isEmpty() && !state.status.equals("RUNNING")) {
            drawCenterText(g, state.message);
        }
    }

    private void drawSnake(Graphics2D g, List<Point> body, Color bodyColor, Color headColor) {
        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            g.setColor(i == 0 ? headColor : bodyColor);
            g.fillRoundRect(p.x * CELL + 1, p.y * CELL + 1, CELL - 2, CELL - 2, 8, 8);
        }
    }

    private void drawCenterText(Graphics2D g, String text) {
        int w = getWidth();
        int h = getHeight();
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, h / 2 - 50, w, 100);

        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(text, (w - fm.stringWidth(text)) / 2, h / 2 + fm.getAscent() / 3);
    }
}
