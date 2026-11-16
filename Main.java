import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Swing windows should always be created on the Swing thread
        SwingUtilities.invokeLater(() -> new MenuFrame().setVisible(true));
    }
}
